package ru.tbank.pp.integration.provider.wildberries;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import ru.tbank.dto.HasSku;
import ru.tbank.dto.SearchQuery;
import ru.tbank.dto.SimilarProducts;
import ru.tbank.dto.UpdatePriceRequest;
import ru.tbank.dto.UpdatePriceResponse;
import ru.tbank.pp.integration.config.WbProviderConfig;
import ru.tbank.dto.NormalizedReference;
import ru.tbank.dto.ProductInfo;
import ru.tbank.dto.ProductReference;
import ru.tbank.pp.integration.provider.ProductProvider;
import ru.tbank.pp.integration.provider.exception.InvalidProductReferenceException;
import ru.tbank.pp.integration.provider.exception.ProductNotFoundException;
import ru.tbank.pp.integration.provider.exception.ProviderCommunicationException;
import ru.tbank.pp.integration.provider.wildberries.product.PriceSchema;
import ru.tbank.pp.integration.provider.wildberries.product.ProductSchema;
import ru.tbank.pp.integration.provider.wildberries.product.Response;
import ru.tbank.pp.integration.provider.wildberries.product.SizeSchema;
import ru.tbank.pp.model.ProductsMarketplace;
import ru.tbank.pp.model.ProductsUrl;

@Slf4j
@Service
@RequiredArgsConstructor
public class WildberriesProvider implements ProductProvider {
    private final RestClient restClient;
    private final WbProviderConfig config;
    private final ImageService imageService;

    private static final String PRODUCT_URL = "https://wildberries.ru/catalog/%d/detail.aspx";

    private String buildProductUrl(Long productId) {
        return String.format(PRODUCT_URL, productId);
    }

    private String buildNmString(List<? extends HasSku> references) {
        return references.stream()
                .map(HasSku::getSku)
                .distinct()
                .collect(Collectors.joining(";"));
    }

    private String buildNmStringFromIds(List<Long> ids) {
        return ids.stream()
                .map(Object::toString)
                .collect(Collectors.joining(";"));
    }

    private void parseProductUrl(ProductReference productReference) {
        UriComponents uri = UriComponentsBuilder
                .fromUriString(productReference.getUrl())
                .build();

        List<String> segments = uri.getPathSegments();
        int detailIndex = segments.lastIndexOf("detail.aspx");
        if (detailIndex > 0) {
            productReference.setSku(segments.get(detailIndex - 1));
        } else {
            log.error("Invalid product url: {}", productReference.getUrl());
            throw new IllegalArgumentException("Unsupported url " +  productReference.getUrl());
        }

        productReference.setOptionId(uri.getQueryParams().getFirst("size"));
    }

    private Response sendProductRequest(String nm) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("api-android.wildberries.ru")
                        .path("/__internal/card/cards/v4/detail")
                        .queryParam("appType", 32)
                        .queryParam("curr", "rub")
                        .queryParam("dest", config.getDest())
                        .queryParam("lang", "ru")
                        .queryParam("locale", "ru")
                        .queryParam("nm", nm)
                        .build()
                )
                .cookie("x_wbaas_token", config.getToken())
                .header("User-Agent", config.getUserAgent())
                .retrieve()
                .body(Response.class);
    }

    private List<Long> sendIdenticalProductRequest(long id) {
        Long[] result = restClient.get()
        .uri(uriBuilder -> uriBuilder
                .scheme("https")
                .host("identical-products.wildberries.ru")
                .path("api/v1/identical")
                .queryParam("nmID", id)
                .build()
        )
        .cookie("x_wbaas_token", config.getToken())
        .header("User-Agent", config.getUserAgent())
                .retrieve()
        .body(Long[].class);
        if (result == null) {
            throw new ProviderCommunicationException("Got no response (wtf)");
        }
        return Arrays.asList(result);
    }

    private Response sendSearchRequest(SearchQuery query) {
        URI uri = UriComponentsBuilder.newInstance()
                .scheme("https")
                .host("api-android.wildberries.ru")
                .path("/__internal/search/exactmatch/ru/common/v14/search")
                .queryParam("curr", "rub")
                .queryParam("dest", config.getDest())
                .queryParam("lang", "ru")
                .queryParam("locale", "ru")
                .queryParam("page", query.getOffset())
                .queryParam("query", query.getQuery())
                .queryParam("resultset", "catalog")
                .queryParam("appType", 32)
                .queryParam("sort", "popular")
                .queryParam("suppressSpellcheck", "false")
                .queryParam("limit", query.getLimit())
                .build()
                .encode()
                .toUri();


        String rawResponse = restClient.get()
                .uri(uri)
                .header("User-Agent", config.getUserAgent())
                .cookie("x_wbaas_token", config.getToken())
                .retrieve()
                .body(String.class);

        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(rawResponse, Response.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private SizeSchema findOption(ProductSchema schema, String optionId) {
        SizeSchema result;
        if (optionId != null) {
            long numOption;
            try {
                numOption = Long.parseLong(optionId);
            } catch (NumberFormatException e) {
                throw new InvalidProductReferenceException("Invalid option id " + optionId);
            }
            result = schema.getSizes().stream()
                    .filter(sizeSchema -> sizeSchema.getOptionId().equals(numOption))
                    .findFirst()
                    .orElse(null);
            if (result == null) {
                log.debug("Provided option id not found {}", optionId);
                throw new IllegalArgumentException("Option id not found " + optionId);
            }
        } else {
            if (schema.getSizes().isEmpty()) {
                log.debug("No options found.");
                result = new SizeSchema();
                result.setOrigName(null);
                result.setOptionId(null);
                result.setPrice(null);
            } else {
                result = schema.getSizes().getFirst();
            }
        }
        return result;
    }

    private UpdatePriceResponse parsePrice(ProductSchema product, Long id, String optionId, Instant lastUpdate) {
        UpdatePriceResponse result = new UpdatePriceResponse();
        result.setId(id);
        result.setDate(lastUpdate);

        SizeSchema option = findOption(product, optionId);
        if (option != null) {
            PriceSchema price = option.getPrice();
            result.setPrice(BigDecimal.valueOf(price.getProduct(), 2));
        }
        return result;
    }

    private ProductInfo parseInfo(ProductSchema product, String optionId) {
        ProductInfo result = ProductInfo.builder()
                .name(product.getName())
                .category(product.getEntity())
                .brand(product.getBrand())
                .rating(product.getReviewRating().toString())
                .sku(product.getId().toString())
                .marketplace(ProductsMarketplace.WILDBERRIES)
                .imageUrl(imageService.getBigUrl(product.getId()))
                .previewUrl(imageService.get268x328Url(product.getId()))
                .build();
        SizeSchema option = findOption(product, optionId);
        if (option.getOptionId() != null) {
            result.setOptionId(option.getOptionId().toString());
        }
        result.setOptionName(option.getOrigName());
        PriceSchema price = option.getPrice();
        result.setUrl(buildProductUrl(product.getId()));
        if (price != null) {
            result.setPrice(price.getProduct());
        }
        return result;
    }

    @Override
    public NormalizedReference normalize(ProductReference productReference) {
        if (StringUtils.hasText(productReference.getUrl())) {
            parseProductUrl(productReference);
        } else if (!StringUtils.hasText(productReference.getSku())) {
            log.error("Invalid product reference: {}", productReference);
            throw new InvalidProductReferenceException("Invalid product reference: " + productReference);
        }

        return new NormalizedReference(
                productReference.getSku(),
                productReference.getMarketplace(),
                productReference.getOptionId()
        );
    }

    @Override
    public ProductReference parseUrl(ProductReference productReference) {
        if (StringUtils.hasText(productReference.getUrl())) {
            parseProductUrl(productReference);
        } else {
            log.error("Invalid product url: {}", productReference.getUrl());
            throw new InvalidProductReferenceException("Invalid product url: " + productReference.getUrl());
        }
        productReference.setUrl(buildProductUrl(Long.parseLong(productReference.getSku())));
        return productReference;
    }

    @Override
    public ProductInfo getProductInfo(ProductReference productReference) {
        NormalizedReference normalizedReference = normalize(productReference);

        List<ProductSchema> productList = sendProductRequest(normalizedReference.getSku()).getProducts();
        if (productList.isEmpty()) {
            log.debug("No products found for sku: {}", normalizedReference.getSku());
            throw new ProductNotFoundException("Product not found: " + normalizedReference.getSku());
        }

        return parseInfo(
                productList.getFirst(),
                normalizedReference.getOptionId()
        );
    }

    @Override
    public List<ProductInfo> getProductInfo(List<NormalizedReference> referenceList) {
        Map<String, List<String>> skuToOptions = referenceList.stream()
                .collect(Collectors.groupingBy(NormalizedReference::getSku, Collectors.mapping(NormalizedReference::getOptionId, Collectors.toList())));

        Response wbResponse = sendProductRequest(buildNmString(referenceList));
        if (wbResponse.getProducts().isEmpty()) {
            log.debug("No products found. Couldn't get ProductInfo.");
            throw new ProductNotFoundException("No products found.");
        }

        List<ProductInfo> result = new ArrayList<>(referenceList.size());
        wbResponse.getProducts().forEach(product ->
                skuToOptions.get(product.getId().toString())
                        .forEach(option ->
                                result.add(parseInfo(product, option))
                        )
        );
        return result;
    }

    @Override
    public List<UpdatePriceResponse> getPriceInfo(List<UpdatePriceRequest> referenceList) {
        Map<String, List<UpdatePriceRequest>> skuToOptions = referenceList.stream()
                .collect(
                        Collectors.groupingBy(UpdatePriceRequest::getSku)
                );

        Response wbResponse = sendProductRequest(buildNmString(referenceList));
        if (wbResponse.getProducts().isEmpty()) {
            log.debug("No products found. Couldn't get PriceInfo.");
            throw new ProductNotFoundException("No products found.");
        }
        Instant requestTime = Instant.now();

        List<UpdatePriceResponse> result = new ArrayList<>(referenceList.size());
        wbResponse.getProducts().forEach(product ->
                skuToOptions.get(product.getId().toString())
                        .forEach(priceObject ->
                                result.add(parsePrice(product, priceObject.getId(), priceObject.getOptionId(), requestTime))
                        )
        );
        return result;
    }

    @Override
    public SimilarProducts getSimilarProducts(NormalizedReference productReference) {
        List<Long> ids = sendIdenticalProductRequest(Long.parseLong(productReference.getSku()));
        if (ids.isEmpty()) {
            log.debug("No products found for sku: {}", productReference.getSku());
            return new SimilarProducts(List.of());
        }
        Response wbResponse = sendProductRequest(buildNmStringFromIds(ids));
        if (wbResponse.getProducts().isEmpty()) {
            log.debug("No similar products found for sku: {}", productReference.getSku());
            throw new ProductNotFoundException("Similar products not found: " + productReference.getSku());
        }

        return new SimilarProducts(
                wbResponse.getProducts().stream()
                .map(product -> parseInfo(product, null))
                .collect(Collectors.toCollection(ArrayList::new))
        );
    }

    @Override
    public SimilarProducts search(SearchQuery query) {
        Response wbResponse = sendSearchRequest(query);
        if (wbResponse == null || wbResponse.getProducts() == null || wbResponse.getProducts().isEmpty()) {
            log.debug("No similar products found for query: {}", query);
            throw new ProductNotFoundException("Similar products not found for query: " + query);
        }

        return new SimilarProducts(
                wbResponse.getProducts().stream()
                        .map(product -> parseInfo(product, null))
                        .collect(Collectors.toCollection(ArrayList::new))
        );
    }
}

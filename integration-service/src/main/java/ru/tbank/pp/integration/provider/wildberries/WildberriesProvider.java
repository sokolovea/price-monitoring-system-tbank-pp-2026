package ru.tbank.pp.integration.provider.wildberries;

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
import ru.tbank.pp.integration.config.WbProviderConfig;
import ru.tbank.pp.integration.dto.NormalizedReference;
import ru.tbank.pp.integration.dto.PriceInfo;
import ru.tbank.pp.integration.dto.ProductInfo;
import ru.tbank.pp.integration.dto.ProductReference;
import ru.tbank.pp.integration.provider.ProductProvider;
import ru.tbank.pp.integration.provider.ProviderType;
import ru.tbank.pp.integration.provider.exception.InvalidProductReferenceException;
import ru.tbank.pp.integration.provider.exception.ProductNotFoundException;
import ru.tbank.pp.integration.provider.exception.ProviderCommunicationException;
import ru.tbank.pp.integration.provider.wildberries.product.PriceSchema;
import ru.tbank.pp.integration.provider.wildberries.product.ProductSchema;
import ru.tbank.pp.integration.provider.wildberries.product.SizeSchema;
import ru.tbank.pp.integration.provider.wildberries.product.Response;

@Slf4j
@Service
@RequiredArgsConstructor
public class WildberriesProvider implements ProductProvider {
    private final RestClient restClient;
    private final WbProviderConfig config;
    private final ImageService imageService;

    private static final String PRODUCT_URL = "https://wildberries.ru/catalog/%d/detail.aspx";
    private static final String PRODUCT_URL_WITH_OPTION = PRODUCT_URL + "?optionId=%d";

    private String buildProductUrl(Long productId, Long optionId) {
        if (optionId == null) {
            return String.format(PRODUCT_URL, productId);
        } else {
            return String.format(PRODUCT_URL_WITH_OPTION, productId, optionId);
        }
    }

    private String buildNmString(List<NormalizedReference> references) {
        return references.stream()
                .map(NormalizedReference::getSku)
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

    private Response sendProductRequest(String nm) { return restClient.get()
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

    private PriceInfo parsePrice(ProductSchema product, String optionId, Instant lastUpdate) {
        PriceInfo result = new PriceInfo();
        result.setSku(product.getId().toString());
        result.setLastUpdate(lastUpdate);

        SizeSchema option = findOption(product, optionId);
        if (option != null) {
            PriceSchema price = option.getPrice();
            result.setPrice(price.getProduct());
            result.setOptionId(option.getOptionId().toString());
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
                .marketplace(ProviderType.WILDBERRIES)
                .imageUrl(imageService.getBigUrl(product.getId()))
                .previewUrl(imageService.get268x328Url(product.getId()))
                .build();
        SizeSchema option = findOption(product, optionId);
        if (option.getOptionId() != null) {
            result.setOptionId(option.getOptionId().toString());
        }
        result.setOptionName(option.getOrigName());
        PriceSchema price = option.getPrice();
        result.setUrl(buildProductUrl(product.getId(), option.getOptionId()));
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
    public List<PriceInfo> getPriceInfo(List<NormalizedReference> referenceList) {
        Map<String, List<String>> skuToOptions = referenceList.stream()
                .collect(Collectors.groupingBy(NormalizedReference::getSku, Collectors.mapping(NormalizedReference::getOptionId, Collectors.toList())));

        Response wbResponse = sendProductRequest(buildNmString(referenceList));
        if (wbResponse.getProducts().isEmpty()) {
            log.debug("No products found. Couldn't get PriceInfo.");
            throw new ProductNotFoundException("No products found.");
        }
        Instant requestTime = Instant.now();

        List<PriceInfo> result = new ArrayList<>(referenceList.size());
        wbResponse.getProducts().forEach(product ->
                skuToOptions.get(product.getId().toString())
                        .forEach(option ->
                                result.add(parsePrice(product, option, requestTime))
                        )
        );
        return result;
    }

    @Override
    public List<ProductInfo> getSimilarProducts(NormalizedReference productReference) {
        List<Long> ids = sendIdenticalProductRequest(Long.parseLong(productReference.getSku()));
        Response wbResponse = sendProductRequest(buildNmStringFromIds(ids));
        if (wbResponse.getProducts().isEmpty()) {
            log.debug("No similar products found for sku: {}", productReference.getSku());
            throw new ProductNotFoundException("Similar products not found: " + productReference.getSku());
        }

        return wbResponse.getProducts().stream()
                .map(product -> parseInfo(product, null))
                .collect(Collectors.toCollection(ArrayList::new));
    }
}

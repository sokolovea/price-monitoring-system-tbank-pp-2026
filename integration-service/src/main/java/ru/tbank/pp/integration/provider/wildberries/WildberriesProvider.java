package ru.tbank.pp.integration.provider.wildberries;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import ru.tbank.pp.integration.dto.PriceInfo;
import ru.tbank.pp.integration.dto.ProductInfo;
import ru.tbank.pp.integration.dto.ProductReference;
import ru.tbank.pp.integration.provider.ProductProvider;
import ru.tbank.pp.integration.provider.ProviderType;
import ru.tbank.pp.integration.provider.wildberries.product.PriceSchema;
import ru.tbank.pp.integration.provider.wildberries.product.ProductSchema;
import ru.tbank.pp.integration.provider.wildberries.product.SizeSchema;
import ru.tbank.pp.integration.provider.wildberries.product.WbResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class WildberriesProvider implements ProductProvider {
    private final RestClient restClient;
    private final ImageService imageService;

    private String buildProductUrl(Long productId, Long optionId) {
        final String PRODUCT_URL = "https://wildberries.ru/catalog/%d/detail.aspx";
        final String PRODUCT_URL_WITH_OPTION = PRODUCT_URL + "?optionId=%d";

        if (optionId == null) {
            return String.format(PRODUCT_URL, productId);
        } else {
            return String.format(PRODUCT_URL_WITH_OPTION, productId, optionId);
        }
    }

    private void parseUrl(ProductReference productReference) {
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

    private WbResponse sendProductRequest(String nm) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("api-android.wildberries.ru")
                        .path("/__internal/card/cards/v4/detail")
                        .queryParam("appType", 32)
                        .queryParam("curr", "rub")
                        .queryParam("dest", -1257786)
                        .queryParam("lang", "ru")
                        .queryParam("locale", "ru")
                        .queryParam("nm", nm)
                        .build()
                ).retrieve()
                .body(WbResponse.class);
    }

    private SizeSchema findOption(ProductSchema schema, Long optionId) {
        SizeSchema result;
        if (optionId != null) {
            result = schema.getSizes().stream()
                    .filter(sizeSchema -> sizeSchema.getOptionId().equals(optionId))
                    .findFirst()
                    .orElse(null);
            if (result == null) {
                log.debug("Provided option id not found {}", optionId);
                throw new IllegalArgumentException("Option id not found " + optionId);
            }
        } else {
            result = schema.getSizes().getFirst();
        }
        return result;
    }

    @Override
    public ProductInfo getProductInfo(ProductReference productReference) {
        if (productReference.getUrl() != null && !productReference.getUrl().isEmpty()) {
            parseUrl(productReference);
        } else if (productReference.getSku().isEmpty()) {
            log.error("Invalid product reference: {}", productReference);
            throw new IllegalArgumentException("Invalid product reference");
        }

        List<ProductSchema> productList = sendProductRequest(productReference.getSku()).getProducts();
        if (productList.isEmpty()) {
            return null;
        }
        ProductSchema product = productList.getFirst();
        ProductInfo result = ProductInfo.builder()
                .name(product.getName())
                .category(product.getEntity())
                .brand(product.getBrand())
                .rating(product.getReviewRating().toString())
                .sku(product.getId().toString())
                .marketplace(ProviderType.Wildberries)
                .imageUrl(imageService.getBigUrl(product.getId()))
                .previewUrl(imageService.get268x328Url(product.getId()))
                .build();

        Long optionId = null;
        if (productReference.getOptionId() != null) {
            try {
                optionId = Long.parseLong(productReference.getOptionId());
            } catch (NumberFormatException e) {
                log.error("Invalid option id provided: {}", productReference);
                throw new IllegalArgumentException("Invalid option id: " + productReference.getOptionId());
            }
        }
        SizeSchema option = findOption(product, optionId);
        result.setOptionId(option.getOptionId().toString());
        result.setOptionName(option.getOrigName());
        result.setUrl(buildProductUrl(product.getId(), option.getOptionId()));

        PriceSchema price = option.getPrice();
        if (price != null) {
            result.setPrice(price.getProduct());
        }
        return result;
    }

    @Override
    public List<ProductInfo> getProductInfoList(List<ProductReference> referenceList) {
        return List.of();
    }

    @Override
    public List<PriceInfo> getPriceInfo(List<ProductReference> referenceList) {
        return List.of();
    }
}

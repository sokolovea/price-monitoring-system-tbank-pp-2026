package ru.tbank.pp.integration.provider.wildberries;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
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

    private static final String PRODUCT_URL = "https://wildberries.ru/catalog/%d/detail.aspx";
    private static final String PRODUCT_URL_WITH_OPTION = PRODUCT_URL + "?optionId=%d";

    private String buildProductUrl(Long productId, Long optionId) {
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
        if (StringUtils.hasText(productReference.getUrl())) {
            parseUrl(productReference);
        } else if (!StringUtils.hasText(productReference.getSku())) {
            log.error("Invalid product reference: {}", productReference);
            //TODO: add exception handler
            throw new IllegalArgumentException("Invalid product reference: " + productReference);
        }

        List<ProductSchema> productList = sendProductRequest(productReference.getSku()).getProducts();
        if (productList.isEmpty()) {
            //TODO: 404
            return null;
        }
        ProductSchema product = productList.getFirst();
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

        Long optionId;
        if (StringUtils.hasText(productReference.getOptionId())) {
            try {
                optionId = Long.parseLong(productReference.getOptionId());
            } catch (NumberFormatException e) {
                log.error("Invalid option id provided: {}", productReference);
                throw new IllegalArgumentException("Invalid option id: " + productReference.getOptionId());
            }
        } else {
            optionId = null;
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

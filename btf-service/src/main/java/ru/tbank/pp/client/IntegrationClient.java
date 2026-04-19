package ru.tbank.pp.client;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import ru.tbank.dto.ProductInfo;
import ru.tbank.dto.ProductReference;
import ru.tbank.dto.SearchQuery;
import ru.tbank.dto.SimilarProducts;
import ru.tbank.pp.model.ProductsUrl;

@Slf4j
@Component
public class IntegrationClient {
    private final RestClient restClient;

    public IntegrationClient(@Qualifier("integration") RestClient restClient) {
        this.restClient = restClient;
    }

   public Optional<ProductInfo> sendProductRequest(ProductReference productReference) {
        ResponseEntity<ProductInfo> response = restClient.post()
                .uri("/product")
                .accept(MediaType.APPLICATION_JSON)
                .body(productReference)
                .retrieve()
                .toEntity(ProductInfo.class);

        Optional<ProductInfo> result;
        if (response.getStatusCode().is2xxSuccessful()) {
            result = Optional.ofNullable(response.getBody());
        } else {
            result = Optional.empty();
            log.debug("/product request failed! Response: {}", response.getBody());
        }
        return result;
   }

    public Optional<SimilarProducts> sendSimilarRequest(ProductReference productReference) {
        ResponseEntity<SimilarProducts> response = restClient.post()
                .uri("/similar")
                .accept(MediaType.APPLICATION_JSON)
                .body(productReference)
                .retrieve()
                .toEntity(SimilarProducts.class);

        Optional<SimilarProducts> result;
        if (response.getStatusCode().is2xxSuccessful()) {
            result = Optional.ofNullable(response.getBody());
        } else {
            result = Optional.empty();
            log.debug("similar request failed! Response: {}", response.getBody());
        }
        return result;
    }

    public Optional<SimilarProducts> sendSearchRequest(SearchQuery searchQuery) {
        ResponseEntity<SimilarProducts> response = restClient.post()
                .uri("/search")
                .accept(MediaType.APPLICATION_JSON)
                .body(searchQuery)
                .retrieve()
                .toEntity(SimilarProducts.class);

        Optional<SimilarProducts> result;
        if (response.getStatusCode().is2xxSuccessful()) {
            result = Optional.ofNullable(response.getBody());
        } else {
            result = Optional.empty();
            log.debug("search request failed! Response: {}", response.getBody());
        }
        return result;
    }

    public Optional<ProductReference> sendParseRequest(ProductsUrl url) {
        ResponseEntity<ProductReference> response = restClient.post()
                .uri("/parse")
                .accept(MediaType.APPLICATION_JSON)
                .body(url)
                .retrieve()
                .toEntity(ProductReference.class);

        Optional<ProductReference> result;
        if (response.getStatusCode().is2xxSuccessful()) {
            result = Optional.ofNullable(response.getBody());
        } else {
            result = Optional.empty();
            log.debug("Parse request failed! Response: {}", response.getBody());
        }
        return result;
    }
}

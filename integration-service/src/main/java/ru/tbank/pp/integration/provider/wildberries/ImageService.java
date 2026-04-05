package ru.tbank.pp.integration.provider.wildberries;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

//TODO: move those to separate package
@JsonIgnoreProperties(ignoreUnknown = true)
class Response {
    public MediaInfo recommend;
}

@JsonIgnoreProperties(ignoreUnknown = true)
class MediaInfo {
    public List<Method> mediabasket_route_map;
}

@JsonIgnoreProperties(ignoreUnknown = true)
class Method {
    public String method;
    public List<Host> hosts;
}

@JsonIgnoreProperties(ignoreUnknown = true)
class Host {
    public Long vol_range_from;
    public Long vol_range_to;
    public String host;
}

@Getter
@AllArgsConstructor
class ProductId {
    long vol;
    long part;
}

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {
    private final RestClient restClient;
    private List<Host> hosts;

    private Response sendHostRequest() {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("cdn.wbbasket.ru")
                        .path("/api/v3/upstreams")
                        .build()
                ).retrieve()
                .body(Response.class);
    }

    @PostConstruct
    private void setHosts() {
        Response hostRequest = sendHostRequest();
        if (hostRequest == null) {
            log.error("Couldn't request hosts");
            // TODO: Change runtime exception
            throw new RuntimeException("Request failed");
        }
        //TODO check null recommend
        Method result = hostRequest.recommend.mediabasket_route_map.stream()
                .filter(routeMap -> routeMap.method.equals("range"))
                .findFirst()
                .orElse(null);
        if (result == null) {
            log.error("No \"range\" method found.");
            throw new RuntimeException("Couldn't set hosts");
        }

        hosts = result.hosts;
    }

    private String findHost(long vol) {
        int left = -1;
        int right = hosts.size();
        while (right - left > 1) {
            int mid = (right + left) / 2;
            if (hosts.get(mid).vol_range_from < vol) {
                left = mid;
            } else {
                right = mid;
            }
        }
        if (left == -1 || hosts.get(left).vol_range_to < vol) {
            log.error("Couldn't find host with vol {}", vol);
            throw new IllegalArgumentException("Invalid vol " + vol);
        }
        return hosts.get(left).host;
    }

    private ProductId parseId(long id) {
        long vol = id / 100_000;
        long part = id / 1_000;
        return new ProductId(
            vol,
            part
        );
    }

    private String getUrl(long productId, String imageSize) {
        ProductId product = parseId(productId);
        return UriComponentsBuilder.newInstance()
                .scheme("https")
                .host(findHost(product.vol))
                .pathSegment(
                        "vol{vol}",
                        "part{part}",
                        "{id}",
                        "images",
                        imageSize,
                        "1.webp"
                ).buildAndExpand(product.vol, product.part, productId)
                .toUriString();
    }

    public String getBigUrl(long productId) {
        return getUrl(productId, "big");
    }

    public String get268x328Url(long productId) {
        return getUrl(productId, "c246x328");
    }
}

package ru.tbank.pp.integration.provider.wildberries;

import java.util.List;
import lombok.AllArgsConstructor; import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import ru.tbank.pp.integration.provider.exception.ProviderCommunicationException;
import ru.tbank.pp.integration.provider.wildberries.image.Host;
import ru.tbank.pp.integration.provider.wildberries.image.Method;
import ru.tbank.pp.integration.provider.wildberries.image.MethodType;
import ru.tbank.pp.integration.provider.wildberries.image.Response;

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
    private Method method;

    /**
     * To get vol out of product id you have to remove first five digits.
     * i.e. for id: 281014628
     *  vol would be equal to 2810
     *  part would be equal to 281014
     */
    private static final long VOL_IGNORE_LEN = 100_000;
    public static final long PART_IGNORE_LEN = 1_000;

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

    private void setHosts() {
        Response hostRequest = sendHostRequest();
        if (hostRequest == null ||
                hostRequest.recommend == null ||
                hostRequest.recommend.mediabasket_route_map == null) {
            log.error("Couldn't request hosts");
            throw new ProviderCommunicationException("Request failed");
        }

        method = hostRequest.recommend.mediabasket_route_map.stream()
                .findFirst()
                .orElseThrow(() -> {
                    log.debug("Couldn't find mediabasket route map.");
                    return new ProviderCommunicationException("Couldn't find mediabasket_route_map");
                });
    }

    private String findHostWithRange(long vol) {
        List<Host> hosts = method.hosts;

        int left = -1;
        int right = hosts.size();
        while (right - left > 1) {
            int mid = (right + left) / 2;
            if (hosts.get(mid).vol_range_from <= vol) {
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

    private String findHostWithMod(long part) {
        int hostIdx = Math.toIntExact(part % method.hosts.size());
        return method.hosts.get(hostIdx).host;
    }

    private String findHost(ProductId productId) {
        if (method == null) {
            setHosts();
        }

        return switch (method.method) {
            case RANGE -> findHostWithRange(productId.vol);
            case MOD -> findHostWithMod(productId.part);
            case UNKNOWN -> {
                log.debug("Unknown method {}", method);
                throw new IllegalArgumentException("Unknown method");
            }
        };
    }

    private ProductId parseId(long id) {
        long vol = id / VOL_IGNORE_LEN;
        long part = id / PART_IGNORE_LEN;
        return new ProductId(
            vol,
            part
        );
    }

    private String getUrl(long productId, String imageSize) {
        ProductId product = parseId(productId);
        return UriComponentsBuilder.newInstance()
                .scheme("https")
                .host(findHost(product))
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

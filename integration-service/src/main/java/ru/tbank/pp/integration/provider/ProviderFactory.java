package ru.tbank.pp.integration.provider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import ru.tbank.pp.integration.provider.exception.UnsupportedProviderException;
import ru.tbank.pp.integration.provider.wildberries.WildberriesProvider;
import ru.tbank.pp.model.ProductsMarketplace;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProviderFactory {
    private final ApplicationContext context;

    public ProductProvider getProvider(ProductsMarketplace provider) {
        return switch (provider) {
            case WILDBERRIES -> context.getBean(WildberriesProvider.class);
            default -> {
                log.debug("Unsupported provider {}", provider);
                throw new UnsupportedProviderException("Unsupported provider :" + provider);
            }
        };
    }
}

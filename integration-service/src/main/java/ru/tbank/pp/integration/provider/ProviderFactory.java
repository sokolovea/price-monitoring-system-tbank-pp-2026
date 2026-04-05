package ru.tbank.pp.integration.provider;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import ru.tbank.pp.integration.provider.wildberries.WildberriesProvider;

@Component
@RequiredArgsConstructor
public class ProviderFactory {
    private final ApplicationContext context;

    public ProductProvider getProvider(ProviderType provider) {
        return switch (provider) {
            case WILDBERRIES -> context.getBean(WildberriesProvider.class);
        };
    }
}

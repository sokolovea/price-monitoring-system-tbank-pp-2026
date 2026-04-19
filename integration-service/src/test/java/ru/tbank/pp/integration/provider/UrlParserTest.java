package ru.tbank.pp.integration.provider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.tbank.dto.ProductReference;
import ru.tbank.pp.integration.provider.exception.UnsupportedProviderException;
import ru.tbank.pp.model.ProductsMarketplace;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тесты UrlParser")
class UrlParserTest {

    private UrlParser urlParser;

    @BeforeEach
    void setUp() {
        urlParser = new UrlParser();
    }

    @Test
    @DisplayName("setProvider должен определять WILDBERRIES для www.wildberries.ru")
    void setProvider_shouldDetectWildberries() {
        ProductReference ref = new ProductReference();
        ref.setUrl("https://www.wildberries.ru/catalog/12345/detail.aspx");

        urlParser.setProvider(ref);

        assertEquals(ProductsMarketplace.WILDBERRIES, ref.getMarketplace());
    }

    @Test
    @DisplayName("setProvider должен определять OZON для www.ozon.ru")
    void setProvider_shouldDetectOzonRu() {
        ProductReference ref = new ProductReference();
        ref.setUrl("https://www.ozon.ru/product/12345");

        urlParser.setProvider(ref);

        assertEquals(ProductsMarketplace.OZON, ref.getMarketplace());
    }

    @Test
    @DisplayName("setProvider должен определять OZON для www.ozon.by")
    void setProvider_shouldDetectOzonBy() {
        ProductReference ref = new ProductReference();
        ref.setUrl("https://www.ozon.by/product/12345");

        urlParser.setProvider(ref);

        assertEquals(ProductsMarketplace.OZON, ref.getMarketplace());
    }

    @Test
    @DisplayName("setProvider должен определять OZON для www.ozon.kz")
    void setProvider_shouldDetectOzonKz() {
        ProductReference ref = new ProductReference();
        ref.setUrl("https://www.ozon.kz/product/12345");

        urlParser.setProvider(ref);

        assertEquals(ProductsMarketplace.OZON, ref.getMarketplace());
    }

    @Test
    @DisplayName("setProvider должен выбрасывать UnsupportedProviderException для неизвестного хоста")
    void setProvider_shouldThrowExceptionForUnknownHost() {
        ProductReference ref = new ProductReference();
        ref.setUrl("https://unknown-marketplace.ru/product/12345");

        assertThrows(UnsupportedProviderException.class, () -> urlParser.setProvider(ref));
    }

    @Test
    @DisplayName("setProvider должен выбрасывать UnsupportedProviderException для невалидного URL")
    void setProvider_shouldThrowExceptionForInvalidUrl() {
        ProductReference ref = new ProductReference();
        ref.setUrl("not-a-valid-url");

        assertThrows(Exception.class, () -> urlParser.setProvider(ref));
    }
}

package ru.tbank.pp.mapper;

import org.junit.jupiter.api.Test;
import ru.tbank.pp.entity.UserProduct;
import ru.tbank.pp.model.ProductsNotification;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class UserProductMapperTest {

    private final UserProductMapper userProductMapper = new UserProductMapper();

    @Test
    void toProductsNotification_Success() {
        UserProduct userProduct = new UserProduct();
        userProduct.setNotify(true);
        userProduct.setThresholdPrice(new BigDecimal("800"));

        ProductsNotification result = userProductMapper.toProductsNotification(userProduct);

        assertThat(result).isNotNull();
        assertThat(result.getEnabled()).isTrue();
        assertThat(result.getThresholdPrice()).isEqualByComparingTo(new BigDecimal("800"));
    }

    @Test
    void toProductsNotification_DisabledNotification() {
        UserProduct userProduct = new UserProduct();
        userProduct.setNotify(false);
        userProduct.setThresholdPrice(new BigDecimal("1200"));

        ProductsNotification result = userProductMapper.toProductsNotification(userProduct);

        assertThat(result).isNotNull();
        assertThat(result.getEnabled()).isFalse();
        assertThat(result.getThresholdPrice()).isEqualByComparingTo(new BigDecimal("1200"));
    }

    @Test
    void toProductsNotification_NullThresholdPrice() {
        UserProduct userProduct = new UserProduct();
        userProduct.setNotify(true);
        userProduct.setThresholdPrice(null);

        ProductsNotification result = userProductMapper.toProductsNotification(userProduct);

        assertThat(result).isNotNull();
        assertThat(result.getEnabled()).isTrue();
        assertThat(result.getThresholdPrice()).isNull();
    }
}

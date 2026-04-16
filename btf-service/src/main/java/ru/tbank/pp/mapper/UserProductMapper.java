package ru.tbank.pp.mapper;

import org.springframework.stereotype.Component;
import ru.tbank.pp.entity.UserProduct;
import ru.tbank.pp.model.ProductsNotification;

@Component
public class UserProductMapper {
    public ProductsNotification toProductsNotification(UserProduct userProduct) {
        var productsNotification = new ProductsNotification();
        productsNotification.setEnabled(userProduct.getNotify());
        productsNotification.setThresholdPrice(userProduct.getThresholdPrice());
        return productsNotification;
    }
}

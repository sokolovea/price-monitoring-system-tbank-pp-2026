package ru.tbank.pp.service;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tbank.dto.NotificationRequestDto;
import ru.tbank.dto.UpdatePriceResponse;
import ru.tbank.pp.client.NotificationClient;
import ru.tbank.pp.entity.ProductPrice;
import ru.tbank.pp.entity.UserNotificationId;
import ru.tbank.pp.mapper.ProductPriceMapper;
import ru.tbank.pp.model.ServiceConnectionService;
import ru.tbank.pp.repository.ProductPriceRepository;
import ru.tbank.pp.repository.UserNotificationRepository;
import ru.tbank.pp.repository.UserProductRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductPriceService {
    private final ProductPriceRepository productPriceRepository;
    private final UserProductRepository userProductRepository;
    private final UserNotificationRepository userNotificationRepository;

    private final ProductPriceMapper productPriceMapper;

    private final NotificationClient notificationClient;

    public BigDecimal getCurrentPrice(Long productId) {
        return productPriceRepository.findByProductIdOrderByIdCheckDateDesc(productId).getFirst().getPrice();
    }

    public List<ProductPrice> getLatestPricesUpdatedBefore(Instant date) {
        return productPriceRepository.findLatestPricesBefore(date);
    }

    public List<ProductPrice> getProductPrices(Long productId) {
        return productPriceRepository.findByProductIdOrderByIdCheckDateDesc(productId);
    }

    public void setProductPrice(UpdatePriceResponse updatePriceResponse) {
        var productPrice = productPriceMapper.toProductPrice(updatePriceResponse);
        productPrice = productPriceRepository.save(productPrice);

        var productId = updatePriceResponse.getId();
        var price = updatePriceResponse.getPrice();

        var users = userProductRepository.findUserIdsForNotification(productId, price);
        var product = productPrice.getProduct();

        var notificationRequestDto = new NotificationRequestDto();
        notificationRequestDto.setProductUrl(product.getUrl());
        notificationRequestDto.setProductName(product.getName());
        notificationRequestDto.setProductPhotoUrl(product.getImage());

        //todo переделать под общий вид с сервисами уведомлений
        for (var userId : users) {
            var userNotificationId = new UserNotificationId();
            userNotificationId.setUserId(userId);
            userNotificationId.setNotificationService(ServiceConnectionService.TELEGRAM);

            var internalId = userNotificationRepository.findById(userNotificationId).get().getInternalId();
            notificationRequestDto.setChatId(internalId);
            notificationClient.sendNotification(notificationRequestDto);
        }
    }
}

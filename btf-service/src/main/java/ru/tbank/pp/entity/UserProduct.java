package ru.tbank.pp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "user_products")
@Getter
@Setter
public class UserProduct {

    @EmbeddedId
    private UserProductId id;

    @ManyToOne
    @MapsId("userId")
    private User user;

    @ManyToOne
    @MapsId("productId")
    private Product product;

    @Column(nullable = false)
    private boolean notify;

    @Column(name = "threshold_price", nullable = false)
    private BigDecimal thresholdPrice;
}

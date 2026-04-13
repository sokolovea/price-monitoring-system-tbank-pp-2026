package ru.tbank.pp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Embeddable
public class ProductPriceId implements Serializable {

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "check_date")
    private LocalDateTime checkDate;
}

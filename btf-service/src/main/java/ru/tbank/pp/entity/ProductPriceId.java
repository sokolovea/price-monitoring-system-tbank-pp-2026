package ru.tbank.pp.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Embeddable
@Getter
@Setter
public class ProductPriceId implements Serializable {

    private Long productId;

    private LocalDateTime checkDate;
}

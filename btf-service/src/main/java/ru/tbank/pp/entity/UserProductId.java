package ru.tbank.pp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
public class UserProductId implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "product_id")
    private Long productId;
}

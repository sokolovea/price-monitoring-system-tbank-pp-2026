package ru.tbank.pp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import ru.tbank.pp.model.ProductsMarketplace;

@Entity
@Table(name = "product")
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductsMarketplace marketplace;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false)
    private String article;

    @Column(nullable = false)
    private String description;

    @Column(name = "is_tracked", nullable = false)
    private Boolean isTracked;

    @Column(name = "option_name", nullable = false)
    private String optionName;

    @Column(name = "option_id", nullable = false)
    private String optionId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String image;
}
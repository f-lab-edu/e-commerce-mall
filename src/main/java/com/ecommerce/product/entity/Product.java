package com.ecommerce.product.entity;

import com.ecommerce.common.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @NotNull
    private BigDecimal price;

    @NotNull
    private String sumImg;

    @NotNull
    private String detailImg;

    @NotNull
    private int stock;

    @NotNull
    private int score;

    @NotNull
    private int deliveryFee;

    @NotNull
    private boolean fastDelivery;

    @Builder
    public Product(Long id, String name, BigDecimal price, String sumImg, String detailImg, int stock, int score, int deliveryFee, boolean fastDelivery) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.sumImg = sumImg;
        this.detailImg = detailImg;
        this.stock = stock;
        this.score = score;
        this.deliveryFee = deliveryFee;
        this.fastDelivery = fastDelivery;
    }
}

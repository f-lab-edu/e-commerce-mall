package com.ecommerce.product.entity;

import com.ecommerce.category.entity.Category;
import com.ecommerce.common.BaseTimeEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @NotNull
    private String name;

    @NotNull
    private BigDecimal price;

    @NotNull
    private String sumImg;

    @NotNull
    private String detailImg;

    private String brand;

    @NotNull
    private int stock;

    @NotNull
    @ColumnDefault("0")
    private int score;

    @NotNull
    private int deliveryFee;

    @NotNull
    private Integer fastDelivery;

    @Builder
    public Product(Long id, Category category, String name, BigDecimal price, String sumImg, String detailImg, String brand, int stock, int score, int deliveryFee, Integer fastDelivery) {
        this.id = id;
        this.category = category;
        this.name = name;
        this.price = price;
        this.sumImg = sumImg;
        this.detailImg = detailImg;
        this.brand = brand;
        this.stock = stock;
        this.score = score;
        this.deliveryFee = deliveryFee;
        this.fastDelivery = fastDelivery;
    }
}

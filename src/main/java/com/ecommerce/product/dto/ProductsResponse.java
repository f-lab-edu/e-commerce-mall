package com.ecommerce.product.dto;

import com.ecommerce.product.entity.Product;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class ProductsResponse {

    private Long id;
    private String categoryName;
    private String name;
    private BigDecimal price;
    private String sumImg;
    private String brand;
    private int deliveryFee;
    private Integer fastDelivery;

    public ProductsResponse(Product product) {
        this.id = product.getId();
        this.categoryName = product.getCategory().getName();
        this.name = product.getName();
        this.price = product.getPrice();
        this.sumImg = product.getSumImg();
        this.brand = product.getBrand();
        this.deliveryFee = product.getDeliveryFee();
        this.fastDelivery = product.getFastDelivery();
    }
}

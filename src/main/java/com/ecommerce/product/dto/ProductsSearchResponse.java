package com.ecommerce.product.dto;

import com.ecommerce.product.document.ProductDocument;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class ProductsSearchResponse {
    private String id;
    private String categoryName;
    private String name;
    private BigDecimal price;
    private String sumImg;
    private String brand;
    private int deliveryFee;
    private boolean fastDelivery;

    public ProductsSearchResponse(ProductDocument product) {
        this.id = product.getId();
        this.categoryName = product.getCategoryName();
        this.name = product.getName();
        this.price = product.getPrice();
        this.sumImg = product.getSumImg();
        this.brand = product.getBrand();
        this.deliveryFee = product.getDeliveryFee();
        this.fastDelivery = product.isFastDelivery();
    }
}

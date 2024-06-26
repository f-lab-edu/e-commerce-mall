package com.ecommerce.product.dto;

import java.math.BigDecimal;
import lombok.Getter;

@Getter
public class AddProductRequest {

  private Long categoryId;
  private String name;
  private BigDecimal price;
  private String thumbImg;
  private String detailImg;
  private String brand;
  private int stock;
  private int deliveryFee;
  private Integer fastDelivery;

  public AddProductRequest(Long categoryId, String name, BigDecimal price, String thumbImg,
      String detailImg, String brand, int stock, int deliveryFee, Integer fastDelivery) {
    this.categoryId = categoryId;
    this.name = name;
    this.price = price;
    this.thumbImg = thumbImg;
    this.detailImg = detailImg;
    this.brand = brand;
    this.stock = stock;
    this.deliveryFee = deliveryFee;
    this.fastDelivery = fastDelivery;
  }
}

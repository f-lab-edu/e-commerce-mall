package com.ecommerce.product.dto;

import lombok.Getter;

@Getter
//@Builder
public class UpdateProductRequest {

  //  private Long categoryId;
//  private String name;
//  private BigDecimal price;
  private String thumbImg;
  private String detailImg;
//  private String brand;
//  private int stock;
//  private int deliveryFee;
//  private Integer fastDelivery;

  public UpdateProductRequest(String thumbImg) {
    this.thumbImg = thumbImg;
  }

  public UpdateProductRequest(String thumbImg, String detailImg) {
    this.thumbImg = thumbImg;
    this.detailImg = detailImg;
  }
}

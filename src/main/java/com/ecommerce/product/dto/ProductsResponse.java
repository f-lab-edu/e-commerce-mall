package com.ecommerce.product.dto;

import com.ecommerce.product.entity.Product;
import java.math.BigDecimal;
import lombok.Getter;

@Getter
public class ProductsResponse {

  private Long id;
  private String categoryName;
  private String name;
  private BigDecimal price;
  private String thumbImg;
  private String brand;
  private int deliveryFee;
  private Integer fastDelivery;

  public ProductsResponse(Product product) {
    this.id = product.getId();
    this.categoryName = product.getCategory().getName();
    this.name = product.getName();
    this.price = product.getPrice();
    this.thumbImg = product.getThumbImg();
    this.brand = product.getBrand();
    this.deliveryFee = product.getDeliveryFee();
    this.fastDelivery = product.getFastDelivery();
  }
}

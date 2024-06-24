package com.ecommerce.product.dto;

import com.ecommerce.product.document.ProductDocument;
import java.math.BigDecimal;
import lombok.Getter;

@Getter
public class ProductsSearchResponse {

  private String id;
  private String categoryName;
  private String name;
  private BigDecimal price;
  private String thumbImg;
  private String brand;
  private int deliveryFee;
  private boolean fastDelivery;

  public ProductsSearchResponse(ProductDocument product) {
    this.id = product.getId();
    this.categoryName = product.getCategoryName();
    this.name = product.getName();
    this.price = product.getPrice();
    this.thumbImg = product.getThumbImg();
    this.brand = product.getBrand();
    this.deliveryFee = product.getDeliveryFee();
    this.fastDelivery = product.isFastDelivery();
  }
}

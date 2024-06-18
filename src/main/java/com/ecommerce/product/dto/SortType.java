package com.ecommerce.product.dto;

import co.elastic.clients.elasticsearch._types.SortOrder;
import lombok.Getter;

@Getter
public enum SortType {
  LATEST("createAt", SortOrder.Desc, false),
  LOWPRICE("price", SortOrder.Asc, true),
  HIGHPRICE("price", SortOrder.Desc, false),
  RANK("score", SortOrder.Asc, true);

  private final String fieldName;
  private final SortOrder sortOrder;
  private final boolean asc;

  SortType(String fieldName, SortOrder sortOrder, boolean asc) {
    this.fieldName = fieldName;
    this.sortOrder = sortOrder;
    this.asc = asc;
  }
}

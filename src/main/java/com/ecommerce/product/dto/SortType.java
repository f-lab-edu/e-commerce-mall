package com.ecommerce.product.dto;

import lombok.Getter;
@Getter
public enum SortType {
    LATEST("createdAt", false),
    LOWPRICE("price", true),
    HIGHPRICE("price", false),
    RANK("score", true);

    private final String fieldName;
    private final boolean asc;

    SortType(String fieldName, boolean asc) {
        this.fieldName = fieldName;
        this.asc = asc;
    }
}

package com.ecommerce.product.dto;

import co.elastic.clients.elasticsearch._types.SortOrder;
import lombok.Getter;

@Getter
public enum SortType {
    LATEST("createAt", SortOrder.Desc),
    LOWPRICE("price", SortOrder.Asc),
    HIGHPRICE("price", SortOrder.Desc),
    RANK("score", SortOrder.Asc);

    private final String fieldName;
    private final SortOrder sortOrder;

    SortType(String fieldName, SortOrder sortOrder) {
        this.fieldName = fieldName;
        this.sortOrder = sortOrder;
    }
}

package com.ecommerce.product.dto;

import co.elastic.clients.elasticsearch._types.FieldValue;
import lombok.Getter;

@Getter
public enum DeliveryType {
    FREE("deliveryFee", FieldValue.of(0)),
    FAST("fastDelivery", FieldValue.of(true));

    private final String fieldName;
    private final FieldValue fieldValue;

    DeliveryType(String fieldName, FieldValue fieldValue) {
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
}

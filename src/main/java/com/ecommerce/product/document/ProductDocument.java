package com.ecommerce.product.document;

import com.ecommerce.product.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Document(indexName = "products")
@AllArgsConstructor
@NoArgsConstructor
public class ProductDocument {

    @Id
    private String id;
    @Field(type = FieldType.Keyword)
    private String categoryName;
    @Field(type = FieldType.Text)
    private String name;
    @Field(type = FieldType.Integer)
    private BigDecimal price;
    @Field(type = FieldType.Text, index = false)
    private String sumImg;
    @Field(type = FieldType.Keyword)
    private String brand;
    @Field(type = FieldType.Integer)
    private int score;
    @Field(type = FieldType.Integer)
    private int deliveryFee;
    @Field(type = FieldType.Boolean)
    private boolean fastDelivery;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    private LocalDateTime createdAt;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    private LocalDateTime updatedAt;

    public ProductDocument(Product product) {
        this.id = String.valueOf(product.getId());
        this.categoryName = product.getCategory().getName();
        this.name = product.getName();
        this.price = product.getPrice();
        this.sumImg = product.getSumImg();
        this.brand = product.getBrand();
        this.score = product.getScore();
        this.deliveryFee = product.getDeliveryFee();
        this.fastDelivery = (product.getFastDelivery() == 1);
        this.createdAt = product.getCreatedAt();
        this.updatedAt = product.getUpdatedAt();
    }
}

package com.ecommerce.category.entity;

import com.ecommerce.common.BaseTimeEntity;
import com.ecommerce.product.entity.Product;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private List<Category> child = new ArrayList<>();

    @NotNull
    @ColumnDefault("100")
    private int sortKey;

    @OneToMany(mappedBy = "category")
    private List<Product> products = new ArrayList<>();

    @Builder
    public Category(Long id, String name, Category parent, List<Category> child, int sortKey, List<Product> products) {
        this.id = id;
        this.name = name;
        this.parent = parent;
        this.child = child;
        this.sortKey = sortKey;
        this.products = products;
    }
}

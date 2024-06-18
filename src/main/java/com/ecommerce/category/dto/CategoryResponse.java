package com.ecommerce.category.dto;

import com.ecommerce.category.entity.Category;
import lombok.Getter;

import java.util.List;

@Getter
public class CategoryResponse {

    private long id;
    private String name;
    private List<CategoryResponse> child;
    private int sortKey;

    public CategoryResponse(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.child = category.getChild().stream().map(CategoryResponse::new).toList();
        this.sortKey = category.getSortKey();
    }
}

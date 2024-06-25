package com.ecommerce.category.controller;

import com.ecommerce.category.dto.CategoryResponse;
import com.ecommerce.category.entity.Category;
import com.ecommerce.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("")
    public ResponseEntity<List<CategoryResponse>> readCategories() {
        List<CategoryResponse> categories = categoryService.readCategories().stream()
                .filter((Category category) -> category.getParent() == null)
                .map(CategoryResponse::new)
                .toList();
        return ResponseEntity.ok().body(categories);
    }
}

package com.ecommerce.category.service;

import com.ecommerce.category.dto.CategoryResponse;
import com.ecommerce.category.entity.Category;
import com.ecommerce.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryResponse> readCategories() {

        return categoryRepository.findAllByOrderBySort().stream()
                .filter((Category category) -> category.getParent() == null)
                .map(CategoryResponse::new)
                .toList();
    }
}

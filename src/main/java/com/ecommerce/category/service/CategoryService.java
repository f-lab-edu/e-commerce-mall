package com.ecommerce.category.service;

import com.ecommerce.category.entity.Category;
import com.ecommerce.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> readCategories() {

        return categoryRepository.findAllByOrderBySortKey();
    }
}

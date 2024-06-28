package com.ecommerce.category.service;

import com.ecommerce.category.entity.Category;
import com.ecommerce.category.repository.CategoryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

  private final CategoryRepository categoryRepository;

  public Category findCategoryById(Long id) {
    return categoryRepository.findById(id).orElseThrow();
  }

  public List<Category> readCategories() {

    return categoryRepository.findAllByOrderBySortKey();
  }
}

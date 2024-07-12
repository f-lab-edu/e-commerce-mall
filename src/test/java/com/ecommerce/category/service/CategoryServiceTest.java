package com.ecommerce.category.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ecommerce.category.entity.Category;
import com.ecommerce.category.repository.CategoryRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

  @Mock
  private CategoryRepository categoryRepository;

  @InjectMocks
  private CategoryService categoryService;

  @DisplayName("카테고리 조회에 성공한다.")
  @Test
  void findCategoryByIdSuccess() {
    // given
    Long categoryId = 1L;
    Category category = Category.builder()
        .id(categoryId)
        .name("Test Category")
        .build();

    when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

    // when
    Category foundCategory = categoryService.findCategoryById(categoryId);

    // then
    assertNotNull(foundCategory);
    assertEquals(categoryId, foundCategory.getId());
    assertEquals("Test Category", foundCategory.getName());

    verify(categoryRepository, times(1)).findById(categoryId);
  }

  @DisplayName("카테고리 조회에 실패한다.")
  @Test
  void findCategoryByIdFailure() {
    // given
    Long categoryId = 1L;

    when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

    // when & then
    assertThrows(RuntimeException.class, () -> categoryService.findCategoryById(categoryId));

    verify(categoryRepository, times(1)).findById(categoryId);
  }

  @DisplayName("모든 카테고리 조회에 성공한다.")
  @Test
  void readCategoriesSuccess() {
    // given
    List<Category> categories = new ArrayList<>();
    categories.add(Category.builder()
        .id(1L)
        .name("Test Category1")
        .build());
    categories.add(Category.builder()
        .id(2L)
        .name("Test Category2")
        .build());
    categories.add(Category.builder()
        .id(3L)
        .name("Test Category3")
        .build());

    when(categoryRepository.findAllByOrderBySortKey()).thenReturn(categories);

    // when
    List<Category> retrievedCategories = categoryService.readCategories();

    // then
    assertNotNull(retrievedCategories);
    assertEquals(3, retrievedCategories.size());

    verify(categoryRepository, times(1)).findAllByOrderBySortKey();
  }

  @DisplayName("모든 카테고리 조회에 실패한다.")
  @Test
  void readCategoriesFailure() {
    // given
    when(categoryRepository.findAllByOrderBySortKey()).thenReturn(new ArrayList<>());

    // when
    List<Category> retrievedCategories = categoryService.readCategories();

    // then
    assertNotNull(retrievedCategories);
    assertTrue(retrievedCategories.isEmpty());

    verify(categoryRepository, times(1)).findAllByOrderBySortKey();
  }
}
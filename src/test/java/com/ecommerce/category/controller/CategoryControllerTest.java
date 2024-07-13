package com.ecommerce.category.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ecommerce.category.entity.Category;
import com.ecommerce.category.service.CategoryService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

  private MockMvc mockMvc;

  @Mock
  private CategoryService categoryService;

  @InjectMocks
  private CategoryController categoryController;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(categoryController).build();
  }

  @DisplayName("카테고리 조회에 성공한다.")
  @Test
  void readCategories() throws Exception {
    // given
    List<Category> categories = new ArrayList<>();
    Category category1 = Category.builder()
        .id(1L)
        .name("Test Category1")
        .child(new ArrayList<>())
        .build();
    categories.add(category1);
    categories.add(Category.builder()
        .id(2L)
        .name("Test Category2")
        .parent(category1)
        .child(new ArrayList<>())
        .build());
    categories.add(Category.builder()
        .id(3L)
        .name("Test Category3")
        .child(new ArrayList<>())
        .build());
    when(categoryService.readCategories()).thenReturn(categories);

    // when
    mockMvc.perform(get("/categories")
            .contentType(MediaType.APPLICATION_JSON))
        // then
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1L))
        .andExpect(jsonPath("$[0].name").value("Test Category1"))
        .andExpect(jsonPath("$[1].id").value(3L))
        .andExpect(jsonPath("$[1].name").value("Test Category3"))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$").value(hasSize(2)));
  }
}
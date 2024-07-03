package com.ecommerce.category.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ecommerce.category.entity.Category;
import com.ecommerce.category.repository.CategoryRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest
@AutoConfigureMockMvc
class CategoryControllerTest {

  @Autowired
  CategoryRepository categoryRepository;
  @Autowired
  MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    categoryRepository.deleteAll();
  }

  @DisplayName("카테고리 목록 조회에 성공한다.")
  @Test
  void readCategoriesSuccess() throws Exception {
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
    categoryRepository.saveAll(categories);

    // when
    ResultActions resultActions = mockMvc.perform(get("/categories")
        .contentType(MediaType.APPLICATION_JSON));

    // then
    resultActions
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$", hasSize(3)))
        .andExpect(jsonPath("$[0].id", is(1)))
        .andExpect(jsonPath("$[0].name", is("Test Category1")))
        .andExpect(jsonPath("$[1].id", is(2)))
        .andExpect(jsonPath("$[1].name", is("Test Category2")))
        .andExpect(jsonPath("$[2].id", is(3)))
        .andExpect(jsonPath("$[2].name", is("Test Category3")));
  }

  // TODO
  @DisplayName("카테고리 목록 조회에 실패한다.")
  @Test
  void readCategoriesFailure() {

  }
}
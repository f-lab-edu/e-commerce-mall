package com.ecommerce.product.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ecommerce.product.dto.AddProductRequest;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class ProductControllerTest {

  @Mock
  private ProductService productService;
  @InjectMocks
  private ProductController productController;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
  }

  @DisplayName("상품 등록 성공 테스트")
  @Test
  void addProduct() throws Exception {
    // given
    AddProductRequest request = new AddProductRequest(
        1L,
        "Test Product",
        BigDecimal.valueOf(10000.00),
        "thumb.jpg",
        "detail.jpg",
        "Test Brand",
        10,
        2500,
        0
    );
    Product savedProduct = Product.builder()
        .id(1L)
        .name(request.getName())
        .price(request.getPrice())
        .thumbImg(request.getThumbImg())
        .detailImg(request.getDetailImg())
        .brand(request.getBrand())
        .stock(request.getStock())
        .deliveryFee(request.getDeliveryFee())
        .fastDelivery(request.getFastDelivery()).build();

    when(productService.save(any(AddProductRequest.class))).thenReturn(savedProduct);

    // when
    mockMvc.perform(post("/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(request)))
        // then
        .andExpect(status().isOk());
  }

  @DisplayName("썸네일 변경 성공 테스트")
  @Test
  void updateThumbImg() throws Exception {
    // given
    Long productId = 1L;
    String newThumbImg = "new_thumb_img_url";
    Product updatedProduct = Product.builder()
        .id(productId)
        .name("Test Product")
        .price(BigDecimal.valueOf(100))
        .thumbImg(newThumbImg)
        .detailImg("detail_img_url")
        .brand("TestBrand")
        .stock(10)
        .deliveryFee(0)
        .fastDelivery(0)
        .build();

    when(productService.updateThumbImg(productId, newThumbImg)).thenReturn(updatedProduct);

    // when
    mockMvc.perform(patch("/products/{id}", productId).contentType(MediaType.APPLICATION_JSON)
            .content(newThumbImg))
        // then
        .andExpect(status().isOk());
  }
}
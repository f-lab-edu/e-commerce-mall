package com.ecommerce.product.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ecommerce.category.entity.Category;
import com.ecommerce.product.document.ProductDocument;
import com.ecommerce.product.dto.AddProductRequest;
import com.ecommerce.product.dto.DeliveryType;
import com.ecommerce.product.dto.SortType;
import com.ecommerce.product.dto.UpdateProductRequest;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
//@WebMvcTest(ProductController.class)
class ProductControllerTest {

  private MockMvc mockMvc;

  @Mock
  private ProductService productService;

  @InjectMocks
  private ProductController productController;

  private ObjectMapper objectMapper;

  private Category category;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
    objectMapper = new ObjectMapper();
    category = Category.builder()
        .id(1L)
        .name("Test Category")
        .child(new ArrayList<>())
        .build();
  }

  @DisplayName("상품 등록에 성공한다.")
  @Test
  void addProduct() throws Exception {
    // given
    Long id = 1L;
    String name = "Test Category";
    BigDecimal price = BigDecimal.valueOf(10000);
    String thumbImg = "thumbImg.jpg";
    String detailImg = "detailImg.jpg";
    String brand = "Test brand";
    int stock = 50;
    int deliveryFee = 0;
    Integer fastDelivery = 1;

    AddProductRequest request = new AddProductRequest(1L, name, price, thumbImg, detailImg, brand,
        stock, deliveryFee, fastDelivery);

    Product product = Product.builder().id(1L).build();

    given(productService.save(any(AddProductRequest.class))).willReturn(product);

    // when
    mockMvc.perform(post("/products")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        // then
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(id));
  }

  @DisplayName("썸네일 변경에 성공한다.")
  @Test
  void updateThumbImg() throws Exception {
    String thumbImg = "thumbImage.jpg";
    Product product = Product.builder().id(1L).thumbImg(thumbImg).build();
    UpdateProductRequest request = new UpdateProductRequest(thumbImg);

    given(productService.updateThumbImg(anyLong(), anyString())).willReturn(product);

    mockMvc.perform(patch("/products/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1L))
        .andExpect(jsonPath("$.thumbImg").value(thumbImg));
  }

  @DisplayName("상품 검색에 성공한다.")
  @Test
  void search() throws Exception {
    String keyword = "keyword";
    DeliveryType deliveryType = DeliveryType.FAST;
    SortType sortKey = SortType.HIGHPRICE;

    Product product = Product.builder().category(category).id(1L).deliveryFee(0).fastDelivery(1)
        .build();
    ProductDocument productDocument = new ProductDocument(product);

    SearchHit<ProductDocument> searchHit = new SearchHit<>(null, null, null, 0, null, null, null,
        null, null, null, productDocument);
    List<SearchHit<ProductDocument>> searchHitList = List.of(searchHit);

    SearchHits<ProductDocument> searchHits = mock(SearchHits.class);
    given(searchHits.getSearchHits()).willReturn(searchHitList);
    given(searchHits.getTotalHits()).willReturn(1L);

    given(productService.search(anyString(), any(DeliveryType.class), any(SortType.class)))
        .willReturn(searchHits);

    mockMvc.perform(get("/products/" + sortKey)
            .param("keyword", keyword)
            .param("deliveryType", deliveryType.name()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.count").value(1L))
        .andExpect(jsonPath("$.data[0].id").value(1L));
  }

  @DisplayName("상품 조회에 성공한다.")
  @Test
  void readProducts() throws Exception {
    Long categoryId = 1L;
    SortType sortKey = SortType.HIGHPRICE;
    List<Product> products = List.of(Product.builder().category(category).id(1L).build());

    given(productService.readProducts(anyLong(), any(SortType.class))).willReturn(products);

    mockMvc.perform(get("/products/" + categoryId + "/" + sortKey))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(1L));
  }
}
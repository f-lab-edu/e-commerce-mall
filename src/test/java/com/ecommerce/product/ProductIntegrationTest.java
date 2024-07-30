package com.ecommerce.product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ecommerce.category.entity.Category;
import com.ecommerce.category.repository.CategoryRepository;
import com.ecommerce.jwt.JwtUtil;
import com.ecommerce.member.entity.Role;
import com.ecommerce.product.dto.AddProductRequest;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.repository.ProductDocumentRepository;
import com.ecommerce.product.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

//TODO: Mock 객체가 아닌 실제 서버로 통합 테스트 코드 수정
@SpringBootTest
@AutoConfigureMockMvc
class ProductIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ProductRepository productRepository;
  @Autowired
  private CategoryRepository categoryRepository;
  @Autowired
  private ProductDocumentRepository productDocumentRepository;
  @Autowired
  private JwtUtil jwtUtil;

  private Category category;

  @BeforeEach
  void setUp() throws IOException {
    productRepository.deleteAll();

    category = Category.builder()
        .id(1L)
        .name("Test Category")
        .build();
    categoryRepository.save(category);
  }

  @DisplayName("상품 등록에 성공한다.")
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
    String accessToken = jwtUtil.createAccessToken("test@example.com", Role.ADMIN.name());

    // when
    mockMvc.perform(post("/products")
            .header("Access", accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(request)))
        // then
        .andExpect(status().isOk()).andDo(print());

    // DB
    List<Product> products = productRepository.findAll();
    assertEquals(1, products.size());
    assertEquals(products.get(0).getCategory().getId(), request.getCategoryId());
    assertEquals(products.get(0).getName(), request.getName());
    assertEquals(0, products.get(0).getPrice().compareTo(request.getPrice()));
    assertEquals(products.get(0).getThumbImg(), request.getThumbImg());
    assertEquals(products.get(0).getDetailImg(), request.getDetailImg());
    assertEquals(products.get(0).getBrand(), request.getBrand());
    assertEquals(products.get(0).getStock(), request.getStock());
    assertEquals(products.get(0).getDeliveryFee(), request.getDeliveryFee());
    assertEquals(products.get(0).getFastDelivery(), request.getFastDelivery());
  }

  @DisplayName("상품 썸네일 변경에 성공한다.")
  @Test
  void updateThumbImg() throws Exception {
    // given
    String oldThumbImg = "old_thumb_img_url";
    String newThumbImg = "new_thumb_img_url";
    Product product = Product.builder()
        .category(category)
        .name("Test Product")
        .price(BigDecimal.valueOf(100))
        .thumbImg(oldThumbImg)
        .detailImg("detail_img_url")
        .brand("TestBrand")
        .stock(10)
        .deliveryFee(0)
        .fastDelivery(0)
        .build();
    Product savedProduct = productRepository.save(product);
    String accessToken = jwtUtil.createAccessToken("test@example.com", Role.ADMIN.name());

    // when
    mockMvc.perform(patch("/products/{id}", savedProduct.getId())
            .header("Access", accessToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(newThumbImg))
        // then
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.thumbImg").value(newThumbImg));
  }
}
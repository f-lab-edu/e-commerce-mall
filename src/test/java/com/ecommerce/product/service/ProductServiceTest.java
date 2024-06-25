package com.ecommerce.product.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ecommerce.category.entity.Category;
import com.ecommerce.category.service.CategoryService;
import com.ecommerce.product.dto.AddProductRequest;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.producer.KafkaProducer;
import com.ecommerce.product.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;

class ProductServiceTest {

  @Mock
  private CategoryService categoryService;
  @Mock
  private ProductRepository productRepository;
  @Mock
  private KafkaProducer kafkaProducer;
  @InjectMocks
  private ProductService productService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @DisplayName("상품 저장 성공 테스트")
  @Test
  void save() {
    // given
    AddProductRequest request = new AddProductRequest(
        1L,
        "Test Product",
        new BigDecimal("10000.00"),
        "thumb.jpg",
        "detail.jpg",
        "Test Brand",
        10,
        2500,
        0 // fastDelivery는 Integer 타입이지만 값이 null일 수 있어서 0으로 설정
    );

    Category category = Category.builder()
        .id(1L)
        .name("Test Category")
        .sortKey(100)
        .build();

    when(categoryService.findCategoryById(anyLong())).thenReturn(category);
    when(productRepository.save(any())).thenAnswer((Answer<Product>) invocation -> {
      Product productArgument = invocation.getArgument(0);
      return Product.builder()
          .id(1L)
          .category(productArgument.getCategory())
          .name(productArgument.getName())
          .price(productArgument.getPrice())
          .thumbImg(productArgument.getThumbImg())
          .detailImg(productArgument.getDetailImg())
          .brand(productArgument.getBrand())
          .stock(productArgument.getStock())
          .deliveryFee(productArgument.getDeliveryFee())
          .fastDelivery(productArgument.getFastDelivery()).build();
    });

    // when
    Product savedProduct = productService.save(request);

    // then
    assertThat(savedProduct).isNotNull();
    assertThat(savedProduct.getId()).isEqualTo(1L);
    assertThat(savedProduct.getName()).isEqualTo(request.getName());
    assertThat(savedProduct.getPrice()).isEqualByComparingTo(request.getPrice());
    assertThat(savedProduct.getThumbImg()).isEqualTo(request.getThumbImg());
    assertThat(savedProduct.getDetailImg()).isEqualTo(request.getDetailImg());
    assertThat(savedProduct.getBrand()).isEqualTo(request.getBrand());
    assertThat(savedProduct.getStock()).isEqualTo(request.getStock());
    assertThat(savedProduct.getDeliveryFee()).isEqualTo(request.getDeliveryFee());
    assertThat(savedProduct.getFastDelivery()).isEqualTo(
        0); // fastDelivery는 Integer이지만 값을 0으로 초기화했음을 확인

    verify(productRepository, times(1)).save(any(Product.class));
    verify(kafkaProducer, times(1)).sendProduct(any(Product.class));
  }

  @DisplayName("썸네일 변경 테스트")
  @Test
  void updateThumbImg() {
    // given
    Long productId = 1L;
    String newThumbImg = "new_thumb_img_url";
    Product existingProduct = Product.builder()
        .id(productId)
        .name("Test Product")
        .price(BigDecimal.valueOf(100))
        .thumbImg("old_thumb_img_url")
        .detailImg("detail_img_url")
        .brand("TestBrand")
        .stock(10)
        .deliveryFee(0)
        .fastDelivery(0)
        .build();

    // Mock repository의 findById 메소드가 호출될 때 리턴할 객체 설정
    when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));

    // when
    Product updatedProduct = productService.updateThumbImg(productId, newThumbImg);

    // then
    verify(productRepository, times(1)).findById(productId); // findById 메소드가 1번 호출됐는지 확인
    verify(kafkaProducer, times(1)).sendProduct(
        updatedProduct); // kafkaProduct의 sendProduct 메소드가 1번 호출됐는지 확인

    // 썸네일이 업데이트되었는지 확인
    assert updatedProduct != null;
    assert updatedProduct.getThumbImg().equals(newThumbImg);
  }
}
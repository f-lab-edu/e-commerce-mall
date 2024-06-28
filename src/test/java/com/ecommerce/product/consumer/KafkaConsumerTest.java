package com.ecommerce.product.consumer;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.ecommerce.category.entity.Category;
import com.ecommerce.product.document.ProductDocument;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.repository.ProductDocumentRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class KafkaConsumerTest {

  @Mock
  private ProductDocumentRepository productDocumentRepository;
  @InjectMocks
  private KafkaConsumer kafkaConsumer;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @DisplayName("consume 메서드 성공한다.")
  @Test
  void consume() {
    // given
    Category category = Category.builder()
        .id(1L)
        .name("Test Category")
        .sortKey(100)
        .build();

    Product product = Product.builder()
        .id(1L)
        .category(category)
        .name("Test Product")
        .price(new BigDecimal("10.00"))
        .thumbImg("thumb.jpg")
        .detailImg("detail.jpg")
        .brand("Test Brand")
        .stock(100)
        .score(0)
        .deliveryFee(5000)
        .fastDelivery(1)
        .build();

    ProductDocument productDocument = new ProductDocument(product);

    // when
    kafkaConsumer.consume(productDocument);

    // Then
    verify(productDocumentRepository, times(1)).save(productDocument);
  }
}
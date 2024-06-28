package com.ecommerce.product.producer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.ecommerce.category.entity.Category;
import com.ecommerce.product.document.ProductDocument;
import com.ecommerce.product.entity.Product;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;

class KafkaProducerTest {

  @Mock
  private KafkaTemplate<String, Object> kafkaTemplate; // 가짜 객체

  @InjectMocks
  private KafkaProducer kafkaProducer; // 가짜 객체인 kafkaTemplate을 주입할 객체

  @Captor
  private ArgumentCaptor<ProductDocument> captor;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @DisplayName("KafkaProducer가 Product 전송을 성공한다")
  @Test
  void testSendProduct() {
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
    kafkaProducer.sendProduct(product);

    // then
    verify(kafkaTemplate, times(1)).send(eq("products"), captor.capture());

    ProductDocument capturedProductDocument = captor.getValue();

    assertEquals(productDocument.getId(), capturedProductDocument.getId());
    assertEquals(productDocument.getName(), capturedProductDocument.getName());
    assertEquals(productDocument.getPrice(), capturedProductDocument.getPrice());
    assertEquals(productDocument.getThumbImg(), capturedProductDocument.getThumbImg());
    assertEquals(productDocument.getBrand(), capturedProductDocument.getBrand());
    assertEquals(productDocument.getScore(), capturedProductDocument.getScore());
    assertEquals(productDocument.getDeliveryFee(), capturedProductDocument.getDeliveryFee());
    assertEquals(productDocument.isFastDelivery(), capturedProductDocument.isFastDelivery());
  }
}
package com.ecommerce.product.producer;

import com.ecommerce.product.document.ProductDocument;
import com.ecommerce.product.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducer {

  private static final String TOPIC = "products";

  private final KafkaTemplate<String, Object> kafkaTemplate;

  public void sendProduct(Product product) {
    ProductDocument productDocument = new ProductDocument(product);
    kafkaTemplate.send(TOPIC, productDocument);
  }
}

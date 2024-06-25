package com.ecommerce.product.consumer;

import com.ecommerce.product.document.ProductDocument;
import com.ecommerce.product.repository.ProductDocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumer {

  private final ProductDocumentRepository productDocumentRepository;

  @KafkaListener(topics = "products", groupId = "elasticsearch-product-group")
  public void consume(ProductDocument productDocument) {
    productDocumentRepository.save(productDocument); // 객체 id값이 있으면 insert, 없으면 update
  }

}

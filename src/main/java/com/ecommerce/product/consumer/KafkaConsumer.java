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
    try {
      productDocumentRepository.save(productDocument);
    } catch (Exception e) {
      // 예외 처리 로직 추가
      log.error("Error processing Kafka message: {}", e.getMessage(), e);
      // 여기서 예외 처리 로직을 추가하여 필요한 경우에 대응
      throw new RuntimeException("Error processing Kafka message", e);
    }
  }

}

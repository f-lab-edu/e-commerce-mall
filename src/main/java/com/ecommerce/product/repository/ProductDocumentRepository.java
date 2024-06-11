package com.ecommerce.product.repository;

import com.ecommerce.product.document.ProductDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductDocumentRepository extends ElasticsearchRepository<ProductDocument, String> {
}

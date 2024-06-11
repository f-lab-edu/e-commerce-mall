package com.ecommerce.product.repository;

import com.ecommerce.product.document.ProductDocument;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductDocumentRepository extends ElasticsearchRepository<ProductDocument, String> {
    @Query("{\"multi_match\":{\"query\":\"?0\",\"fields\":[\"categoryName\",\"name\",\"brand\"]}}")
    SearchHits<ProductDocument> search(String keyword);
}

package com.ecommerce.product.service;

import com.ecommerce.product.document.ProductDocument;
import com.ecommerce.product.dto.ProductsSearchResponse;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.repository.ProductDocumentRepository;
import com.ecommerce.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductDocumentRepository productDocumentRepository;


    public Map<String, Object> search(String keyword) {
        SearchHits<ProductDocument> searchHits = productDocumentRepository.search(keyword);

        Map<String, Object> result = new HashMap<>();

        result.put("count", searchHits.getTotalHits());

        List<ProductsSearchResponse> productsSearchResponses = searchHits.getSearchHits().stream()
                .map(hit -> new ProductsSearchResponse(hit.getContent()))
                .toList();

        result.put("data", productsSearchResponses);

        return result;
    }

    @Transactional
    public void sync() {
        List<Product> products = productRepository.findAll();

        for (Product product : products) {
            ProductDocument productDocument = new ProductDocument(product);
            productDocumentRepository.save(productDocument);
        }
    }
}

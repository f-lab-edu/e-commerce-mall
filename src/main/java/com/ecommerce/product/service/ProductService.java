package com.ecommerce.product.service;

import com.ecommerce.product.document.ProductDocument;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.repository.ProductDocumentRepository;
import com.ecommerce.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final ProductDocumentRepository productDocumentRepository;

    @Transactional
    public void sync() {
        List<Product> products = productRepository.findAll();

        for (Product product : products) {
            ProductDocument productDocument = new ProductDocument(product);
            productDocumentRepository.save(productDocument);
        }
    }
}

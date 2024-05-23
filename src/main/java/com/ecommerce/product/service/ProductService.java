package com.ecommerce.product.service;

import com.ecommerce.product.dto.ProductsResponse;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    @PersistenceContext
    private EntityManager entityManager;

    private final ProductRepository productRepository;

    public List<ProductsResponse> readProducts(long id, String sort) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> query = cb.createQuery(Product.class);
        Root<Product> product = query.from(Product.class);

        query.where(cb.equal(product.get("category").get("id"), id));

        if (sort != null) {
            if (sort.equals("latest")) {
                query.orderBy(cb.desc(product.get("createdAt")));
            } else if (sort.equals("lowPrice")) {
                query.orderBy(cb.asc(product.get("price")));
            } else if (sort.equals("highPrice")) {
                query.orderBy(cb.desc(product.get("price")));
            } else { // 인기순(기본 값)
                query.orderBy(cb.desc(product.get("stock")));
            }
        } else {
            query.orderBy(cb.desc(product.get("stock")));
        }

        return entityManager.createQuery(query).getResultList().stream().map(ProductsResponse::new).toList();
    }
}

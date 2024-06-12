package com.ecommerce.product.service;

import com.ecommerce.product.dto.ProductsResponse;
import com.ecommerce.product.dto.SortType;
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

    public List<ProductsResponse> readProducts(long id, SortType sortKey) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> query = cb.createQuery(Product.class);
        Root<Product> product = query.from(Product.class);

        query.where(cb.equal(product.get("category").get("id"), id));

        if (sortKey != null) {
            if(sortKey.isAsc()) {
                query.orderBy(cb.asc(product.get(sortKey.getFieldName())));
            } else {
                query.orderBy(cb.desc(product.get(sortKey.getFieldName())));
            }
        } else {
            query.orderBy(cb.desc(product.get(SortType.RANK.getFieldName())));
        }

        return entityManager.createQuery(query).getResultList().stream().map(ProductsResponse::new).toList();
    }
}

package com.ecommerce.product.controller;

import com.ecommerce.product.dto.ProductsResponse;
import com.ecommerce.product.dto.SortType;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.service.ProductService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

  private final ProductService productService;

  @GetMapping("/{categoryId}/{sortKey}")
  public ResponseEntity<List<ProductsResponse>> readProducts(@PathVariable Long categoryId,
      @PathVariable SortType sortKey) {
    List<Product> products = productService.readProducts(categoryId, sortKey);
    return ResponseEntity.ok().body(products.stream().map(ProductsResponse::new).toList());
  }
}

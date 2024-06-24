package com.ecommerce.product.controller;

import com.ecommerce.product.document.ProductDocument;
import com.ecommerce.product.dto.AddProductRequest;
import com.ecommerce.product.dto.DeliveryType;
import com.ecommerce.product.dto.ProductsResponse;
import com.ecommerce.product.dto.ProductsSearchResponse;
import com.ecommerce.product.dto.SortType;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.service.ProductService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

  private final ProductService productService;

  /**
   * 상품 추가 (판매자만 접근 가능)
   *
   * @param request
   * @return id
   */
  @PostMapping("")
  @Transactional
  public ResponseEntity<?> addProduct(@RequestBody AddProductRequest request) {

    Product product = productService.save(request);

    return ResponseEntity.ok().body(product);
  }

  /**
   * 상품 검색
   *
   * @param sortKey
   * @param keyword
   * @param deliveryType
   * @return
   */
  @GetMapping("/{sortKey}")
  public ResponseEntity<Map<String, Object>> search(@PathVariable SortType sortKey,
      @RequestParam String keyword,
      @RequestParam(required = false) DeliveryType deliveryType) {
    SearchHits<ProductDocument> searchHits = productService.search(keyword, deliveryType, sortKey);

    Map<String, Object> result = new HashMap<>();
    result.put("count", searchHits.getTotalHits());
    List<ProductsSearchResponse> productsSearchResponses = searchHits.getSearchHits().stream()
        .map(hit -> new ProductsSearchResponse(hit.getContent())).toList();
    result.put("data", productsSearchResponses);
    return ResponseEntity.ok().body(result);
  }

  /**
   * 카테고리별 상품 조회
   *
   * @param categoryId
   * @param sortKey
   * @return
   */
  @GetMapping("/{categoryId}/{sortKey}")
  public ResponseEntity<List<ProductsResponse>> readProducts(@PathVariable Long categoryId,
      @PathVariable SortType sortKey) {
    List<Product> products = productService.readProducts(categoryId, sortKey);
    return ResponseEntity.ok().body(products.stream().map(ProductsResponse::new).toList());
  }
}

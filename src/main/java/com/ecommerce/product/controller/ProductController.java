package com.ecommerce.product.controller;

import com.ecommerce.product.document.ProductDocument;
import com.ecommerce.product.dto.DeliveryType;
import com.ecommerce.product.dto.ProductsSearchResponse;
import com.ecommerce.product.dto.SortType;
import com.ecommerce.product.service.ProductService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

  private final ProductService productService;

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
}

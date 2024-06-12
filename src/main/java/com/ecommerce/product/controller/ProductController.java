package com.ecommerce.product.controller;

import com.ecommerce.product.dto.DeliveryType;
import com.ecommerce.product.dto.SortType;
import com.ecommerce.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{sortKey}")
    public ResponseEntity<Map<String, Object>> search(@PathVariable SortType sortKey,
                                                      @RequestParam String keyword,
                                                      @RequestParam(required = false) DeliveryType deliveryType) {
        Map<String, Object> result = productService.search(keyword, deliveryType, sortKey);
        return ResponseEntity.ok().body(result);
    }
}

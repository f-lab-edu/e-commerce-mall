package com.ecommerce.product.controller;

import com.ecommerce.product.dto.DeliveryType;
import com.ecommerce.product.dto.SortType;
import com.ecommerce.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> search(@RequestParam String keyword,
                                                      @RequestParam(required = false) DeliveryType deliveryType,
                                                      @RequestParam(required = false, defaultValue = "RANK") SortType sortType) {
        Map<String, Object> result = productService.search(keyword, deliveryType, sortType);
        return ResponseEntity.ok().body(result);
    }
}

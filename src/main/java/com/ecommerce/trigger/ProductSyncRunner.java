package com.ecommerce.trigger;

import com.ecommerce.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ProductSyncRunner implements CommandLineRunner {
    @Autowired
    private ProductService productService;

    @Override
    public void run(String... args) throws Exception {
        productService.sync();
    }
}

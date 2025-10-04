package com.example.adminapp.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "product-search-service", url = "http://localhost:8082")
public interface ProductClient {

    @PutMapping("/products/{id}/price")
    void updateProductPrice(@PathVariable("id") String productId, @RequestParam double newPrice);

    @GetMapping("/products/{id}/price")
    double getProductPrice(@PathVariable("id") String productId);
}



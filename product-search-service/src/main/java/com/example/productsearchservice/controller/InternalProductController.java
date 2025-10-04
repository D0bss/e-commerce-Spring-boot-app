package com.example.productsearchservice.controller;

import com.example.productsearchservice.model.Product;
import com.example.productsearchservice.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/products")
public class InternalProductController {
    @Autowired
    private ProductRepository productRepository;

    @PostMapping("/batch")
    public ResponseEntity<List<Map<String, Object>>> getProductsByIds(@RequestBody List<String> ids) {
        List<Product> products = productRepository.findAllById(ids);

        // Convert each Product to Map<String, Object>
        List<Map<String, Object>> result = products.stream()
                .map(product -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", product.getId());
                    map.put("name", product.getName());
                    map.put("category", product.getCategory());
                    map.put("price", product.getPrice());
                    map.put("rating", product.getRating());
                    map.put("available", product.isAvailable());
                    map.put("type", product.getType());
                    return map;
                }).toList();

        return ResponseEntity.ok(result);
    }
}
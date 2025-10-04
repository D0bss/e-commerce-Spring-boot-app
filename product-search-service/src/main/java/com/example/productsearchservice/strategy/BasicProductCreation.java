package com.example.productsearchservice.strategy;

import com.example.productsearchservice.dto.ProductDTO;
import com.example.productsearchservice.model.Product;
import org.springframework.stereotype.Component;

public class BasicProductCreation implements ProductCreationStrategy {
    @Override
    public Product createProduct(ProductDTO dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setCategory(dto.getCategory());
        product.setPrice(dto.getPrice());
        product.setRating(dto.getRating());
        product.setAvailable(dto.isAvailable());
        product.setType("basic");
        return product;
    }
}

package com.example.productsearchservice.strategy;

import com.example.productsearchservice.dto.ProductDTO;
import com.example.productsearchservice.model.Product;

public interface ProductCreationStrategy {
    Product createProduct(ProductDTO dto);
}

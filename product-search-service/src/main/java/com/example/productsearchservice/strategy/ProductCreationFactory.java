package com.example.productsearchservice.strategy;

import org.springframework.stereotype.Component;


@Component
public class ProductCreationFactory {



    public ProductCreationStrategy getStrategy(String type) {
        String className = StrategyMapping.STRATEGY_MAP.get(type.toLowerCase());

        if (className == null) {
            throw new IllegalArgumentException("No product strategy found for type: " + type);
        }

        try {
            // Load the class dynamically
            Class<?> clazz = Class.forName(className);
            // Ensure it implements the ProductCreationStrategy interface
            if (!ProductCreationStrategy.class.isAssignableFrom(clazz)) {
                throw new IllegalArgumentException("Class " + className + " does not implement ProductCreationStrategy");
            }
            // Create and return an instance of the class
            return (ProductCreationStrategy) clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create product strategy for type: " + type, e);
        }
    }

}
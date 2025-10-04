package com.example.productsearchservice.strategy;

import java.util.Map;

public class StrategyMapping {
    public static final Map<String, String> STRATEGY_MAP = Map.of(
            "basic", "com.example.productsearchservice.strategy.BasicProductCreation",
            "discounted", "com.example.productsearchservice.strategy.DiscountedProductCreation"
    );
}
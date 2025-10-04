package com.example.adminapp.strategy;

import com.example.adminapp.model.DiscountType;

public class DiscountStrategyFactory {
    public static DiscountStrategy getStrategy(DiscountType type) {
        return switch (type) {
            case FLAT -> new FlatDiscountStrategy();
            case PERCENTAGE -> new PercentageDiscountStrategy();
        };
    }
}

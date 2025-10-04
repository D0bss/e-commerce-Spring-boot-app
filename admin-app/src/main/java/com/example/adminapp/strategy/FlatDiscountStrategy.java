package com.example.adminapp.strategy;

public class FlatDiscountStrategy implements DiscountStrategy {
    public double apply(double originalPrice, double value) {
        return Math.max(0, originalPrice - value);
    }
}


package com.example.adminapp.strategy;

public class PercentageDiscountStrategy implements DiscountStrategy {
    public double apply(double originalPrice, double value) {
        return originalPrice * (1 - value / 100.0);
    }
}

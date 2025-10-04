package com.example.adminapp.strategy;

public interface DiscountStrategy {
    double apply(double originalPrice, double value);
}


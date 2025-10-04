package com.example.checkouttransactionservice.model;

import jakarta.persistence.Embeddable;
import java.math.BigDecimal;

@Embeddable
public class OrderItem {

    private String productId; // Changed from Long to String to match Product model
    private String productName;
    private String category;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private boolean available;
    private String type;
    private double rating;

    // Constructors
    public OrderItem() {
    }

    public OrderItem(String productId, String productName, String category, int quantity, BigDecimal unitPrice, boolean available, String type, double rating) {
        this.productId = productId;
        this.productName = productName;
        this.category = category;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
        this.available = available;
        this.type = type;
        this.rating = rating;
    }

    // Getters and Setters
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        if (this.unitPrice != null) {
            this.totalPrice = this.unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
        if (this.quantity > 0) {
            this.totalPrice = this.unitPrice.multiply(BigDecimal.valueOf(this.quantity));
        }
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}

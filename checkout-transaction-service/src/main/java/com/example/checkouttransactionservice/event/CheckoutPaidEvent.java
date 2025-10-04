package com.example.checkouttransactionservice.event;

import java.math.BigDecimal;

public class CheckoutPaidEvent {
    private final String checkoutId;
    private final Long userId; // Changed from String to Long
    private final BigDecimal totalAmount;

    public CheckoutPaidEvent(String checkoutId, Long userId, BigDecimal totalAmount) { // Changed parameter type to Long
        this.checkoutId = checkoutId;
        this.userId = userId;
        this.totalAmount = totalAmount;
    }

    public String getCheckoutId() {
        return checkoutId;
    }

    public Long getUserId() { // Changed return type to Long
        return userId;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    @Override
    public String toString() {
        return "CheckoutPaidEvent{" +
                "checkoutId=\'" + checkoutId + "\\[\']" +
                ", userId=" + userId + // Adjusted for Long type
                ", totalAmount=" + totalAmount +
                "}";
    }
}


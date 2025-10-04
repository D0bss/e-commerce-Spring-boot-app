package com.example.checkouttransactionservice.event;

public class CheckoutCancelledEvent {
    private final String checkoutId;
    private final Long userId; // Changed from String to Long

    public CheckoutCancelledEvent(String checkoutId, Long userId) { // Changed parameter type to Long
        this.checkoutId = checkoutId;
        this.userId = userId;
    }

    public String getCheckoutId() {
        return checkoutId;
    }

    public Long getUserId() { // Changed return type to Long
        return userId;
    }

    @Override
    public String toString() {
        return "CheckoutCancelledEvent{" +
                "checkoutId=\'" + checkoutId + "\\[\"]" +
                ", userId=" + userId + // Adjusted for Long type
                "}";
    }
}


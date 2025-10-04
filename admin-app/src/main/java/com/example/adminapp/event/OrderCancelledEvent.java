package com.example.adminapp.event;

/**
 * Event class for order cancelled events.
 */
public class OrderCancelledEvent {
    private String checkoutId;
    private Long userId;
    
    // Default constructor for JSON deserialization
    public OrderCancelledEvent() {
    }
    
    public OrderCancelledEvent(String checkoutId, Long userId) {
        this.checkoutId = checkoutId;
        this.userId = userId;
    }
    
    // Getters and setters
    public String getCheckoutId() {
        return checkoutId;
    }
    
    public void setCheckoutId(String checkoutId) {
        this.checkoutId = checkoutId;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    @Override
    public String toString() {
        return "OrderCancelledEvent{" +
                "checkoutId='" + checkoutId + '\'' +
                ", userId=" + userId +
                '}';
    }
}

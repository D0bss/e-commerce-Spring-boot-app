package com.example.adminapp.event;

/**
 * Event class for order created events.
 */
public class OrderCreatedEvent {
    private String checkoutId;
    private Long userId;
    
    // Default constructor for JSON deserialization
    public OrderCreatedEvent() {
    }
    
    public OrderCreatedEvent(String checkoutId, Long userId) {
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
        return "OrderCreatedEvent{" +
                "checkoutId='" + checkoutId + '\'' +
                ", userId=" + userId +
                '}';
    }
}

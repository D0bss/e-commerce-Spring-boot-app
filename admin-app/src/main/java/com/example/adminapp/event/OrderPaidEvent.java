package com.example.adminapp.event;

/**
 * Event class for order paid events.
 */
public class OrderPaidEvent {
    private String checkoutId;
    private Long userId;
    private double totalAmount;
    
    // Default constructor for JSON deserialization
    public OrderPaidEvent() {
    }
    
    public OrderPaidEvent(String checkoutId, Long userId, double totalAmount) {
        this.checkoutId = checkoutId;
        this.userId = userId;
        this.totalAmount = totalAmount;
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
    
    public double getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    @Override
    public String toString() {
        return "OrderPaidEvent{" +
                "checkoutId='" + checkoutId + '\'' +
                ", userId=" + userId +
                ", totalAmount=" + totalAmount +
                '}';
    }
}

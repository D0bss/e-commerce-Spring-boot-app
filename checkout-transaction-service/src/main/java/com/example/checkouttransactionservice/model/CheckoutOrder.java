package com.example.checkouttransactionservice.model;

import com.example.checkouttransactionservice.enums.CheckoutStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "checkout_orders")
public class CheckoutOrder {

    @Id
    private String id;

    @Column(nullable = false)
    private Long userId;

    // Fields from User model
    private String name;
    private String email;
    private String address;
    private boolean isValidated;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "checkout_order_items", joinColumns = @JoinColumn(name = "checkout_order_id"))
    private List<OrderItem> items = new ArrayList<>();

    @Column(nullable = false)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CheckoutStatus status;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public CheckoutOrder() {
        this.id = UUID.randomUUID().toString();
        this.status = CheckoutStatus.PENDING;
        this.items = new ArrayList<>();
        this.totalAmount = BigDecimal.ZERO;
    }

    // Builder Pattern (Inner Static Class) - updated for aligned fields
    public static class Builder {
        private Long userId;
        private String name;
        private String email;
        private String address;
        private boolean isValidated;
        private List<OrderItem> items = new ArrayList<>();
        private BigDecimal totalAmount = BigDecimal.ZERO;

        public Builder(Long userId) {
            this.userId = userId;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Builder isValidated(boolean isValidated) {
            this.isValidated = isValidated;
            return this;
        }

        public Builder items(List<OrderItem> items) {
            this.items = items;
            this.totalAmount = items.stream()
                                    .map(OrderItem::getTotalPrice)
                                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            return this;
        }
        
        public Builder addItem(OrderItem item) {
            this.items.add(item);
            this.totalAmount = this.totalAmount.add(item.getTotalPrice());
            return this;
        }

        public CheckoutOrder build() {
            CheckoutOrder order = new CheckoutOrder();
            order.userId = this.userId;
            order.name = this.name;
            order.email = this.email;
            order.address = this.address;
            order.isValidated = this.isValidated;
            order.items = this.items;
            
            order.totalAmount = items.stream()
                                    .map(OrderItem::getTotalPrice)
                                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            if (order.totalAmount.compareTo(BigDecimal.ZERO) < 0) {
                order.totalAmount = BigDecimal.ZERO;
            }
            return order;
        }
    }

    // Getters and Setters for User model fields
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isValidated() {
        return isValidated;
    }

    public void setValidated(boolean validated) {
        isValidated = validated;
    }

    // Existing Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public CheckoutStatus getStatus() {
        return status;
    }

    public void setStatus(CheckoutStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CheckoutOrder that = (CheckoutOrder) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "CheckoutOrder{" +
                "id=\'" + id + "\'," +
                "userId=\'" + userId + "\'," +
                "name=\'" + name + "\'," +
                "status=" + status +
                ", totalAmount=" + totalAmount +
                '}';
    }
}

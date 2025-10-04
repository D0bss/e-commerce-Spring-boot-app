package com.example.checkouttransactionservice.repository;

import com.example.checkouttransactionservice.model.CheckoutOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CheckoutOrderRepository extends JpaRepository<CheckoutOrder, String> {
    List<CheckoutOrder> findByUserId(Long userId); // Changed to Long
    List<CheckoutOrder> findByUserIdOrderByCreatedAtDesc(Long userId); // Added for cancellation logic
}


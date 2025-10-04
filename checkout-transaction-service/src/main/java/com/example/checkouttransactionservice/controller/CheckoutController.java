package com.example.checkouttransactionservice.controller;

import com.example.checkouttransactionservice.enums.CheckoutStatus;
import com.example.checkouttransactionservice.model.CheckoutOrder;
import com.example.checkouttransactionservice.service.CheckoutService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/transactions") 
public class CheckoutController {

    private static final Logger logger = LoggerFactory.getLogger(CheckoutController.class);
    private final CheckoutService checkoutService;

    @Autowired
    public CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    // Endpoint that matches the TransactionServiceClient in user-app
    @PostMapping("/checkout")
    public ResponseEntity<CheckoutOrder> createTransactionCheckout(
            @RequestParam("userId") Long userId,
            @RequestBody List<String> productIds) {
        
        logger.info("Checkout request received - userId: {}, productIds: {}", userId, productIds);
        
        CheckoutOrder createdOrder = checkoutService.initiateCheckout(userId, productIds);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }

    // Endpoint that matches the TransactionServiceClient in user-app
    @PostMapping("/cancel")
    public ResponseEntity<Void> cancelTransactionCheckout(
            @RequestParam Long userId) {
        
        logger.info("Cancel request received - userId: {}", userId);
        
        checkoutService.cancelUserCheckout(userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<CheckoutOrder> createCheckout(@RequestBody CheckoutOrder checkoutOrder) {
        CheckoutOrder createdCheckout = checkoutService.createCheckout(checkoutOrder);
        return new ResponseEntity<>(createdCheckout, HttpStatus.CREATED);
    }

    @GetMapping("/{checkoutId}")
    public ResponseEntity<CheckoutOrder> getCheckoutById(@PathVariable String checkoutId) {
        CheckoutOrder checkout = checkoutService.getCheckoutById(checkoutId);
        return ResponseEntity.ok(checkout);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CheckoutOrder>> getCheckoutsByUserId(@PathVariable Long userId) {
        List<CheckoutOrder> checkouts = checkoutService.getCheckoutsByUserId(userId);
        return ResponseEntity.ok(checkouts);
    }

    @PutMapping("/{checkoutId}")
    public ResponseEntity<CheckoutOrder> updateCheckout(@PathVariable String checkoutId, @RequestBody CheckoutOrder checkoutOrderUpdates) {
        CheckoutOrder updatedCheckout = checkoutService.updateCheckout(checkoutId, checkoutOrderUpdates);
        return ResponseEntity.ok(updatedCheckout);
    }

    @PostMapping("/{checkoutId}/cancel")
    public ResponseEntity<CheckoutOrder> cancelCheckoutById(@PathVariable String checkoutId) {
        CheckoutOrder cancelledCheckout = checkoutService.cancelCheckout(checkoutId);
        return ResponseEntity.ok(cancelledCheckout);
    }

    @PutMapping("/{checkoutId}/status")
    public ResponseEntity<CheckoutOrder> updateOrderStatus(@PathVariable String checkoutId, @RequestBody Map<String, String> payload) {
        String statusString = payload.get("status");
        if (statusString == null) {
            throw new IllegalArgumentException("Status must be provided in the request body.");
        }
        try {
            CheckoutStatus newStatus = CheckoutStatus.valueOf(statusString.toUpperCase());
            CheckoutOrder updatedOrder = checkoutService.updateOrderStatus(checkoutId, newStatus);
            return ResponseEntity.ok(updatedOrder);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status value: " + statusString + ". Valid statuses are: PENDING, AWAITING_PAYMENT, PAID, PROCESSING, SHIPPED, DELIVERED, CANCELLED, FAILED");
        }
    }
}

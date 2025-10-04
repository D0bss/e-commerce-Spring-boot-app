package com.example.checkouttransactionservice.service;

import com.example.checkouttransactionservice.model.CheckoutOrder;
import com.example.checkouttransactionservice.model.OrderItem;
import com.example.checkouttransactionservice.enums.CheckoutStatus;
import com.example.checkouttransactionservice.event.CheckoutCancelledEvent;
import com.example.checkouttransactionservice.event.CheckoutCreatedEvent;
import com.example.checkouttransactionservice.event.CheckoutPaidEvent;
import com.example.checkouttransactionservice.exception.ResourceNotFoundException;
import com.example.checkouttransactionservice.feign.ProductServiceClient;
import com.example.checkouttransactionservice.feign.UserServiceClient;
import com.example.checkouttransactionservice.integration.EventPublisher;
import com.example.checkouttransactionservice.repository.CheckoutOrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class CheckoutService {

    private static final Logger logger = LoggerFactory.getLogger(CheckoutService.class);

    private final CheckoutOrderRepository checkoutOrderRepository;
    private final EventPublisher eventPublisher;
    private final ProductServiceClient productServiceClient;
    private final UserServiceClient userServiceClient;
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String mailSenderEmail;

    @Autowired
    public CheckoutService(CheckoutOrderRepository checkoutOrderRepository,
                           EventPublisher eventPublisher,
                           ProductServiceClient productServiceClient,
                           UserServiceClient userServiceClient,
                           JavaMailSender javaMailSender) {
        this.checkoutOrderRepository = checkoutOrderRepository;
        this.eventPublisher = eventPublisher;
        this.productServiceClient = productServiceClient;
        this.userServiceClient = userServiceClient;
        this.javaMailSender = javaMailSender;
    }

    private void sendCheckoutConfirmationEmail(CheckoutOrder order) {
        if (order.getEmail() == null || order.getEmail().isBlank()) {
            logger.warn("Cannot send confirmation email for order {} as customer email is missing.", order.getId());
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(mailSenderEmail);
            message.setTo(order.getEmail());
            message.setSubject("Order Confirmation - Your Order #" + order.getId());

            StringBuilder emailText = new StringBuilder();
            emailText.append("Dear ").append(order.getName() != null ? order.getName() : "Customer").append(",\n\n");
            emailText.append("Thank you for your order!\n\n");
            emailText.append("Order ID: ").append(order.getId()).append("\n");
            emailText.append("Status: ").append(order.getStatus()).append("\n");
            emailText.append("Total Amount: $").append(order.getTotalAmount()).append("\n\n");
            emailText.append("Items:\n");
            for (OrderItem item : order.getItems()) {
                emailText.append("- ").append(item.getProductName()).append(" (Quantity: ").append(item.getQuantity()).append(", Price: $").append(item.getUnitPrice()).append(")\n");
            }
            emailText.append("\nShipping Address: ").append(order.getAddress() != null ? order.getAddress() : "N/A").append("\n");
            emailText.append("\nThank you for shopping with us!\n");
            emailText.append("VoaygeMasters Team");

            message.setText(emailText.toString());
            javaMailSender.send(message);
            logger.info("Checkout confirmation email sent successfully to {} for order {}", order.getEmail(), order.getId());
        } catch (Exception e) {
            logger.error("Failed to send checkout confirmation email for order {}: {}", order.getId(), e.getMessage(), e);
        }
    }

    @Transactional
    public CheckoutOrder initiateCheckoutWithToken(String authToken) {
        logger.info("Initiating checkout with Authorization token");

        // Extract token if it has a Bearer prefix
        String token = authToken;
        if (authToken.startsWith("Bearer ")) {
            token = authToken.substring(7);
        }

        // Validate token
        boolean isValid = userServiceClient.isValidToken(token);
        if (!isValid) {
            logger.error("Invalid or expired token");
            throw new IllegalStateException("Invalid or expired token");
        }

        // Get user details using token
        Map<String, Object> userMap = userServiceClient.getUserByToken(token);
        if (userMap == null || userMap.isEmpty()) {
            logger.error("User data not found or empty for token");
            throw new ResourceNotFoundException("User not found or user data is empty for token");
        }

        Long userId = ((Number) userMap.get("id")).longValue();
        String name = (String) userMap.get("name");
        String email = (String) userMap.get("email");
        String address = (String) userMap.get("address"); // Address can be null
        Boolean isValidated = (Boolean) userMap.getOrDefault("isValidated", false);

        if (name == null || email == null) {
            logger.error("User name or email is missing from fetched user data for token");
            throw new RuntimeException("Essential user details (name, email) are missing for token");
        }

        // Get user's product IDs using token
        List<String> productIds = userServiceClient.getUserProductIds(token);
        if (productIds == null || productIds.isEmpty()) {
            logger.warn("No products found in user's list for token");
            throw new ResourceNotFoundException("No products found in user's list");
        }

        List<OrderItem> orderItems = new ArrayList<>();

        for (String productId : productIds) {
            try {
                Map<String, Object> productMap = productServiceClient.getProductById(productId);
                if (productMap != null && !productMap.isEmpty()) {
                    String pId = (String) productMap.getOrDefault("id", productId);
                    String pName = (String) productMap.get("name");
                    String pCategory = (String) productMap.get("category");
                    Object priceObj = productMap.get("price");
                    Double pRating = ((Number) productMap.getOrDefault("rating", 0.0)).doubleValue();
                    Boolean pAvailable = (Boolean) productMap.getOrDefault("available", true);
                    String pType = (String) productMap.get("type");

                    BigDecimal pPrice;
                    if (priceObj instanceof BigDecimal) {
                        pPrice = (BigDecimal) priceObj;
                    } else if (priceObj instanceof Number) {
                        pPrice = BigDecimal.valueOf(((Number) priceObj).doubleValue());
                    } else if (priceObj instanceof String) {
                        try {
                            pPrice = new BigDecimal((String) priceObj);
                        } catch (NumberFormatException nfe) {
                            logger.error("Invalid price format for productId: {}: {}", productId, priceObj);
                            continue;
                        }
                    } else {
                        logger.error("Price is missing or of an unknown type for productId: {}", productId);
                        continue;
                    }

                    if (pName == null || pPrice == null) {
                        logger.warn("Product name or price is missing for productId: {}, skipping item.", productId);
                        continue;
                    }

                    OrderItem item = new OrderItem(
                            pId,
                            pName,
                            pCategory,
                            1, // Default quantity
                            pPrice,
                            pAvailable,
                            pType,
                            pRating
                    );
                    orderItems.add(item);
                } else {
                    logger.warn("Product details not found or empty for productId: {}, skipping item.", productId);
                }
            } catch (Exception e) {
                logger.error("Error fetching or processing product details for productId: {}: {}", productId, e.getMessage(), e);
            }
        }

        if (orderItems.isEmpty()) {
            logger.error("No valid products found for checkout for token");
            throw new ResourceNotFoundException("None of the provided product IDs could be resolved to valid products");
        }

        CheckoutOrder.Builder orderBuilder = new CheckoutOrder.Builder(userId)
                .name(name)
                .email(email)
                .address(address) // Address can be null here
                .isValidated(isValidated)
                .items(orderItems);

        CheckoutOrder newOrder = orderBuilder.build();
        CheckoutOrder savedOrder = checkoutOrderRepository.save(newOrder);
        logger.info("Checkout initiated successfully with token. OrderId: {}, UserId: {}", savedOrder.getId(), savedOrder.getUserId());
        eventPublisher.publish(new CheckoutCreatedEvent(savedOrder.getId(), savedOrder.getUserId()), "order.created");
        return savedOrder;
    }

    @Transactional
    public CheckoutOrder initiateCheckout(Long userId, List<String> productIds) {
        logger.info("Initiating checkout for userId: {} with productIds: {}", userId, productIds);

        // Transform productIds to productDataList with default quantity 1
        List<Map<String, Object>> productDataList = new ArrayList<>();
        if (productIds != null) {
            for (String productId : productIds) {
                Map<String, Object> productData = new HashMap<>();
                productData.put("productId", productId);
                productData.put("quantity", 1); // Default quantity
                productDataList.add(productData);
            }
        }

        Map<String, Object> userMap;
        try {
            userMap = userServiceClient.getUserById(userId);
            if (userMap == null || userMap.isEmpty()) {
                logger.error("User data not found or empty for userId: {}", userId);
                throw new ResourceNotFoundException("User not found or user data is empty for id: " + userId);
            }
        } catch (Exception e) {
            logger.error("Error fetching user details for userId: {}: {}", userId, e.getMessage());
            throw new RuntimeException("Could not retrieve user details for user ID: " + userId, e);
        }

        String name = (String) userMap.get("name");
        String email = (String) userMap.get("email");
        String address = (String) userMap.get("address"); // Address can be null
        Boolean isValidated = (Boolean) userMap.getOrDefault("isValidated", false);

        if (name == null || email == null) {
            logger.error("User name or email is missing from fetched user data for userId: {}", userId);
            throw new RuntimeException("Essential user details (name, email) are missing for user ID: " + userId);
        }

        // No validation for address - it can be null or empty

        List<OrderItem> orderItems = new ArrayList<>();
        if (productDataList.isEmpty()) {
            logger.warn("Product data list is empty for userId: {}", userId);
        }

        for (Map<String, Object> productData : productDataList) {
            try {
                String productId = (String) productData.get("productId");
                Integer quantity = ((Number) productData.get("quantity")).intValue();

                Map<String, Object> productMap = productServiceClient.getProductById(productId);
                if (productMap != null && !productMap.isEmpty()) {
                    String pId = (String) productMap.getOrDefault("id", productId);
                    String pName = (String) productMap.get("name");
                    String pCategory = (String) productMap.get("category");
                    Object priceObj = productMap.get("price");
                    Double pRating = ((Number) productMap.getOrDefault("rating", 0.0)).doubleValue();
                    Boolean pAvailable = (Boolean) productMap.getOrDefault("available", true);
                    String pType = (String) productMap.get("type");

                    BigDecimal pPrice;
                    if (priceObj instanceof BigDecimal) {
                        pPrice = (BigDecimal) priceObj;
                    } else if (priceObj instanceof Number) {
                        pPrice = BigDecimal.valueOf(((Number) priceObj).doubleValue());
                    } else if (priceObj instanceof String) {
                        try {
                            pPrice = new BigDecimal((String) priceObj);
                        } catch (NumberFormatException nfe) {
                            logger.error("Invalid price format for productId: {}: {}", productId, priceObj);
                            continue;
                        }
                    } else {
                        logger.error("Price is missing or of an unknown type for productId: {}", productId);
                        continue;
                    }

                    if (pName == null || pPrice == null) {
                        logger.warn("Product name or price is missing for productId: {}, skipping item.", productId);
                        continue;
                    }

                    OrderItem item = new OrderItem(
                            pId,
                            pName,
                            pCategory,
                            quantity,
                            pPrice,
                            pAvailable,
                            pType,
                            pRating
                    );
                    orderItems.add(item);
                } else {
                    logger.warn("Product details not found or empty for productId: {}, skipping item.", productId);
                }
            } catch (Exception e) {
                logger.error("Error fetching or processing product details from productData: {}: {}", productData, e.getMessage(), e);
            }
        }

        if (orderItems.isEmpty() && !productDataList.isEmpty()) {
            logger.error("No valid products found for checkout for userId: {}. Product data attempted: {}", userId, productDataList);
            throw new ResourceNotFoundException("None of the provided product IDs could be resolved to valid products.");
        }

        CheckoutOrder.Builder orderBuilder = new CheckoutOrder.Builder(userId)
                .name(name)
                .email(email)
                .address(address) // Address can be null here
                .isValidated(isValidated)
                .items(orderItems);

        CheckoutOrder newOrder = orderBuilder.build();
        CheckoutOrder savedOrder = checkoutOrderRepository.save(newOrder);
        logger.info("Checkout initiated successfully. OrderId: {}, UserId: {}", savedOrder.getId(), savedOrder.getUserId());
        eventPublisher.publish(new CheckoutCreatedEvent(savedOrder.getId(), savedOrder.getUserId()), "order.created");
        return savedOrder;
    }

    @Transactional
    public void cancelCheckoutWithToken(String authToken) {
        logger.info("Attempting to cancel latest active checkout with Authorization token");

        // Extract token if it has a Bearer prefix
        String token = authToken;
        if (authToken.startsWith("Bearer ")) {
            token = authToken.substring(7);
        }

        // Validate token
        boolean isValid = userServiceClient.isValidToken(token);
        if (!isValid) {
            logger.error("Invalid or expired token");
            throw new IllegalStateException("Invalid or expired token");
        }

        // Get user details using token
        Map<String, Object> userMap = userServiceClient.getUserByToken(token);
        if (userMap == null || userMap.isEmpty()) {
            logger.error("User data not found or empty for token");
            throw new ResourceNotFoundException("User not found or user data is empty for token");
        }

        Long userId = ((Number) userMap.get("id")).longValue();

        // Use existing method to cancel by userId
        cancelUserCheckout(userId);
    }

    @Transactional
    public void cancelUserCheckout(Long userId) {
        logger.info("Attempting to cancel latest active checkout for userId: {}", userId);
        List<CheckoutOrder> userOrders = checkoutOrderRepository.findByUserIdOrderByCreatedAtDesc(userId);
        CheckoutOrder orderToCancel = userOrders.stream()
                .filter(order -> order.getStatus() == CheckoutStatus.PENDING || order.getStatus() == CheckoutStatus.AWAITING_PAYMENT)
                .findFirst()
                .orElse(null);

        if (orderToCancel == null) {
            logger.warn("No active cancellable order found for userId: {}", userId);
            throw new ResourceNotFoundException("No active cancellable order found for user ID: " + userId);
        }

        if (orderToCancel.getStatus() == CheckoutStatus.PAID ||
                orderToCancel.getStatus() == CheckoutStatus.SHIPPED ||
                orderToCancel.getStatus() == CheckoutStatus.DELIVERED ||
                orderToCancel.getStatus() == CheckoutStatus.CANCELLED ) {
            logger.warn("Order {} for userId {} cannot be cancelled as it is already {}, shipped, delivered or cancelled.", orderToCancel.getId(), userId, orderToCancel.getStatus());
            throw new IllegalStateException("Order cannot be cancelled as it is already processed or cancelled.");
        }

        orderToCancel.setStatus(CheckoutStatus.CANCELLED);
        CheckoutOrder cancelledOrder = checkoutOrderRepository.save(orderToCancel);
        logger.info("Order {} for userId {} cancelled successfully.", cancelledOrder.getId(), userId);
        eventPublisher.publish(new CheckoutCancelledEvent(cancelledOrder.getId(), cancelledOrder.getUserId()), "order.cancelled");
    }

    @Transactional
    public CheckoutOrder createCheckout(CheckoutOrder checkoutOrderInput) {
        logger.warn("Legacy createCheckout called. Consider using initiateCheckout for user-app integration.");
        CheckoutOrder.Builder orderBuilder = new CheckoutOrder.Builder(checkoutOrderInput.getUserId())
                .name(checkoutOrderInput.getName())
                .email(checkoutOrderInput.getEmail())
                .address(checkoutOrderInput.getAddress()) // Address can be null
                .isValidated(checkoutOrderInput.isValidated())
                .items(checkoutOrderInput.getItems());

        CheckoutOrder newOrder = orderBuilder.build();
        CheckoutOrder savedOrder = checkoutOrderRepository.save(newOrder);
        eventPublisher.publish(new CheckoutCreatedEvent(savedOrder.getId(), savedOrder.getUserId()), "order.created");
        return savedOrder;
    }

    public CheckoutOrder getCheckoutById(String checkoutId) {
        return checkoutOrderRepository.findById(checkoutId)
                .orElseThrow(() -> new ResourceNotFoundException("CheckoutOrder not found with id: " + checkoutId));
    }

    public List<CheckoutOrder> getCheckoutsByUserId(Long userId) {
        return checkoutOrderRepository.findByUserId(userId);
    }

    @Transactional
    public CheckoutOrder updateCheckout(String checkoutId, CheckoutOrder updatedOrderData) {
        logger.warn("Legacy updateCheckout called for orderId: {}.", checkoutId);
        CheckoutOrder checkoutOrder = getCheckoutById(checkoutId);
        if (updatedOrderData.getName() != null) checkoutOrder.setName(updatedOrderData.getName());
        if (updatedOrderData.getEmail() != null) checkoutOrder.setEmail(updatedOrderData.getEmail());
        // Address can be updated to null
        checkoutOrder.setAddress(updatedOrderData.getAddress());

        if (updatedOrderData.getStatus() != null) {
            CheckoutStatus oldStatus = checkoutOrder.getStatus();
            checkoutOrder.setStatus(updatedOrderData.getStatus());
            if (CheckoutStatus.PAID.equals(updatedOrderData.getStatus()) && !CheckoutStatus.PAID.equals(oldStatus)) {
                sendCheckoutConfirmationEmail(checkoutOrder);
                eventPublisher.publish(new CheckoutPaidEvent(checkoutOrder.getId(), checkoutOrder.getUserId(), checkoutOrder.getTotalAmount()), "order.paid");
            }
        }
        return checkoutOrderRepository.save(checkoutOrder);
    }

    @Transactional
    public CheckoutOrder cancelCheckout(String checkoutId) {
        logger.warn("Legacy cancelCheckout by ID called for orderId: {}.", checkoutId);
        CheckoutOrder checkoutOrder = getCheckoutById(checkoutId);
        if (checkoutOrder.getStatus() == CheckoutStatus.PAID ||
                checkoutOrder.getStatus() == CheckoutStatus.SHIPPED ||
                checkoutOrder.getStatus() == CheckoutStatus.DELIVERED) {
            throw new IllegalStateException("Cannot cancel an order that is already paid, shipped, or delivered.");
        }
        checkoutOrder.setStatus(CheckoutStatus.CANCELLED);
        CheckoutOrder cancelledOrder = checkoutOrderRepository.save(checkoutOrder);
        eventPublisher.publish(new CheckoutCancelledEvent(cancelledOrder.getId(), cancelledOrder.getUserId()), "order.cancelled");
        return cancelledOrder;
    }

    @Transactional
    public CheckoutOrder markOrderAsPaid(String checkoutId) {
        CheckoutOrder checkoutOrder = getCheckoutById(checkoutId);
        if (checkoutOrder.getStatus() == CheckoutStatus.PAID) {
            return checkoutOrder; // Already paid
        }
        if (checkoutOrder.getStatus() != CheckoutStatus.PENDING && checkoutOrder.getStatus() != CheckoutStatus.AWAITING_PAYMENT) {
            throw new IllegalStateException("Order cannot be marked as paid. Current status: " + checkoutOrder.getStatus());
        }
        checkoutOrder.setStatus(CheckoutStatus.PAID);
        CheckoutOrder paidOrder = checkoutOrderRepository.save(checkoutOrder);
        sendCheckoutConfirmationEmail(paidOrder);
        eventPublisher.publish(new CheckoutPaidEvent(paidOrder.getId(), paidOrder.getUserId(), paidOrder.getTotalAmount()), "order.paid");
        return paidOrder;
    }

    @Transactional
    public CheckoutOrder updateOrderStatus(String checkoutId, CheckoutStatus newStatus) {
        if (checkoutId == null || checkoutId.isEmpty()) {
            logger.error("Checkout ID cannot be null or empty when updating order status");
            throw new IllegalArgumentException("Checkout ID cannot be null or empty");
        }

        if (newStatus == null) {
            logger.error("New status cannot be null when updating order status");
            throw new IllegalArgumentException("New status cannot be null");
        }

        CheckoutOrder checkoutOrder = getCheckoutById(checkoutId);
        CheckoutStatus oldStatus = checkoutOrder.getStatus();

        // Validate status transitions
        if (oldStatus == CheckoutStatus.CANCELLED && newStatus != CheckoutStatus.CANCELLED) {
            logger.warn("Cannot change status of a cancelled order: {}", checkoutId);
            throw new IllegalStateException("Cannot change status of a cancelled order");
        }

        if (oldStatus == CheckoutStatus.DELIVERED && newStatus != CheckoutStatus.DELIVERED) {
            logger.warn("Cannot change status of a delivered order: {}", checkoutId);
            throw new IllegalStateException("Cannot change status of a delivered order");
        }

        if (oldStatus == CheckoutStatus.SHIPPED &&
                (newStatus != CheckoutStatus.SHIPPED && newStatus != CheckoutStatus.DELIVERED && newStatus != CheckoutStatus.CANCELLED)) {
            logger.warn("Shipped order can only transition to DELIVERED or CANCELLED: {}", checkoutId);
            throw new IllegalStateException("Shipped order can only transition to DELIVERED or CANCELLED");
        }

        checkoutOrder.setStatus(newStatus);
        CheckoutOrder updatedOrder = checkoutOrderRepository.save(checkoutOrder);

        // Publish events based on new status
        if (newStatus == CheckoutStatus.PAID && oldStatus != CheckoutStatus.PAID) {
            sendCheckoutConfirmationEmail(updatedOrder);
            eventPublisher.publish(new CheckoutPaidEvent(updatedOrder.getId(), updatedOrder.getUserId(), updatedOrder.getTotalAmount()), "order.paid");
            logger.info("Order {} marked as PAID, confirmation email sent and event published", checkoutId);
        } else if (newStatus == CheckoutStatus.CANCELLED && oldStatus != CheckoutStatus.CANCELLED) {
            eventPublisher.publish(new CheckoutCancelledEvent(updatedOrder.getId(), updatedOrder.getUserId()), "order.cancelled");
            logger.info("Order {} marked as CANCELLED, event published", checkoutId);
        }

        logger.info("Order {} status updated from {} to {}", checkoutId, oldStatus, newStatus);
        return updatedOrder;
    }
}
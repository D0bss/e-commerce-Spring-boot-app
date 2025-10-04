package com.example.adminapp.event.listener;

import com.example.adminapp.event.OrderCancelledEvent;
import com.example.adminapp.event.OrderCreatedEvent;
import com.example.adminapp.service.AdminNotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

/**
 * Listener for admin-specific checkout order events.
 * This listener consumes events from the admin-specific queues that the checkout service publishes to.
 */
@Component
public class AdminOrderEventListener {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminOrderEventListener.class);
    private final AdminNotificationService notificationService;
    private final ObjectMapper objectMapper;
    
    @Autowired
    public AdminOrderEventListener(AdminNotificationService notificationService, ObjectMapper objectMapper) {
        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
    }
    
    @PostConstruct
    public void init() {
        logger.info("AdminOrderEventListener initialized, listening for events on admin-specific queues: admin.order.created.queue, admin.order.cancelled.queue");
    }
    
    /**
     * Handles order creation events from the admin-specific queue.
     */
    @RabbitListener(queues = "admin.order.created.queue")
    public void handleAdminOrderCreatedEvent(String eventJson) {
        try {
            logger.info("Admin service received order.created event from admin queue: {}", eventJson);
            
            // Parse the event JSON
            OrderCreatedEvent event = objectMapper.readValue(eventJson, OrderCreatedEvent.class);
            
            // Process the event for admin notification
            notificationService.notifyAdminAboutNewOrder(event);
            
            logger.info("Successfully processed admin order.created event. OrderId: {}", 
                    event.getCheckoutId());
        } catch (Exception e) {
            logger.error("Error processing admin order.created event: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Handles order cancelled events from the admin-specific queue.
     */
    @RabbitListener(queues = "admin.order.cancelled.queue")
    public void handleAdminOrderCancelledEvent(String eventJson) {
        try {
            logger.info("Admin service received order.cancelled event from admin queue: {}", eventJson);
            
            // Parse the event JSON
            OrderCancelledEvent event = objectMapper.readValue(eventJson, OrderCancelledEvent.class);
            
            // Process the event for admin notification
            notificationService.notifyAdminAboutCancelledOrder(event);
            
            logger.info("Successfully processed admin order.cancelled event. OrderId: {}", 
                    event.getCheckoutId());
        } catch (Exception e) {
            logger.error("Error processing admin order.cancelled event: {}", e.getMessage(), e);
        }
    }
}

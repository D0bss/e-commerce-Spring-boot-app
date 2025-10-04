package com.example.checkouttransactionservice.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.checkouttransactionservice.event.CheckoutCancelledEvent;
import com.example.checkouttransactionservice.event.CheckoutCreatedEvent;

@Component
public class RabbitMQEventPublisher implements EventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(RabbitMQEventPublisher.class);
    private final RabbitTemplate rabbitTemplate;

    // Define your exchange name here, it should match the one in RabbitMQConfig
    private static final String EXCHANGE_NAME = "checkout.events.exchange";
    
    // Define routing keys for admin notifications
    private static final String ROUTING_KEY_ADMIN_ORDER_CREATED = "admin.order.created";
    private static final String ROUTING_KEY_ADMIN_ORDER_CANCELLED = "admin.order.cancelled";
    
    // Define routing keys for regular events
    private static final String ROUTING_KEY_ORDER_CREATED = "order.created";
    private static final String ROUTING_KEY_ORDER_CANCELLED = "order.cancelled";

    @Autowired
    public RabbitMQEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publish(Object event, String routingKey) {
        try {
            logger.info("Publishing event: {} with routingKey: {} to exchange: {}", event, routingKey, EXCHANGE_NAME);
            
            // Add more detailed logging before sending
            logger.debug("Event details - Class: {}, Content: {}", 
                    event.getClass().getSimpleName(), 
                    event.toString());
            
            // Send the event to the original routing key
            rabbitTemplate.convertAndSend(EXCHANGE_NAME, routingKey, event);
            
            // For created and cancelled events, also publish to admin queues
            if (routingKey.equals(ROUTING_KEY_ORDER_CREATED) && event instanceof CheckoutCreatedEvent) {
                logger.info("Publishing created event to admin queue with routing key: {}", 
                        ROUTING_KEY_ADMIN_ORDER_CREATED);
                
                rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY_ADMIN_ORDER_CREATED, event);
                logger.info("Event published successfully to admin queue with routing key: {}", 
                        ROUTING_KEY_ADMIN_ORDER_CREATED);
            } 
            else if (routingKey.equals(ROUTING_KEY_ORDER_CANCELLED) && event instanceof CheckoutCancelledEvent) {
                logger.info("Publishing cancelled event to admin queue with routing key: {}", 
                        ROUTING_KEY_ADMIN_ORDER_CANCELLED);
                
                rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY_ADMIN_ORDER_CANCELLED, event);
                logger.info("Event published successfully to admin queue with routing key: {}", 
                        ROUTING_KEY_ADMIN_ORDER_CANCELLED);
            }
            
            logger.info("Event published successfully to exchange: {} with routing key: {}", EXCHANGE_NAME, routingKey);
        } catch (AmqpException e) {
            logger.error("RabbitMQ communication error while publishing event to exchange: {} with routing key: {}: {}", 
                    EXCHANGE_NAME, routingKey, e.getMessage(), e);
            // Consider implementing a retry mechanism or fallback strategy here
            throw new RuntimeException("Failed to publish event to message queue", e);
        } catch (Exception e) {
            logger.error("Unexpected error publishing event to exchange: {} with routing key: {}: {}", 
                    EXCHANGE_NAME, routingKey, e.getMessage(), e);
            throw new RuntimeException("Unexpected error publishing event", e);
        }
    }
}

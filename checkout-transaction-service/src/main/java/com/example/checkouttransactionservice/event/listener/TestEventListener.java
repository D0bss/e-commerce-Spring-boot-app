package com.example.checkouttransactionservice.event.listener;

import com.example.checkouttransactionservice.event.CheckoutCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Test listener to verify RabbitMQ events are being published correctly.
 * This is for diagnostic purposes only and can be removed once event publishing is confirmed working.
 */
@Component
public class TestEventListener {
    
    private static final Logger logger = LoggerFactory.getLogger(TestEventListener.class);
    
    @RabbitListener(queues = "order.created.queue")
    public void handleOrderCreatedEvent(CheckoutCreatedEvent event) {
        logger.info("TEST LISTENER: Received order.created event: {}", event);
        // This is just a test listener to verify events are being published
    }
    
    @RabbitListener(queues = "order.paid.queue")
    public void handleOrderPaidEvent(Object event) {
        logger.info("TEST LISTENER: Received order.paid event: {}", event);
        // This is just a test listener to verify events are being published
    }
    
    @RabbitListener(queues = "order.cancelled.queue")
    public void handleOrderCancelledEvent(Object event) {
        logger.info("TEST LISTENER: Received order.cancelled event: {}", event);
        // This is just a test listener to verify events are being published
    }
}

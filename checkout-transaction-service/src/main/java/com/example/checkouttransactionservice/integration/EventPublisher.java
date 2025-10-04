package com.example.checkouttransactionservice.integration;

public interface EventPublisher {
    void publish(Object event, String routingKey);
}


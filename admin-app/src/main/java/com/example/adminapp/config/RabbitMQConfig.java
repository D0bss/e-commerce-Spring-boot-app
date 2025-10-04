package com.example.adminapp.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ configuration for the admin service.
 * This configuration creates bindings to the admin-specific queues that the checkout service publishes to.
 */
@Configuration
public class RabbitMQConfig {

    // Exchange name - must match the one used by the checkout service
    public static final String EXCHANGE_NAME = "checkout.events.exchange";

    // Queue names - admin-specific queues
    public static final String QUEUE_ADMIN_ORDER_CREATED = "admin.order.created.queue";
    public static final String QUEUE_ADMIN_ORDER_CANCELLED = "admin.order.cancelled.queue";

    // Routing keys - must match the ones used by the checkout service
    public static final String ROUTING_KEY_ADMIN_ORDER_CREATED = "admin.order.created";
    public static final String ROUTING_KEY_ADMIN_ORDER_CANCELLED = "admin.order.cancelled";

    @Bean
    public TopicExchange checkoutEventsExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue adminOrderCreatedQueue() {
        return new Queue(QUEUE_ADMIN_ORDER_CREATED, true); // Durable queue
    }

    @Bean
    public Queue adminOrderCancelledQueue() {
        return new Queue(QUEUE_ADMIN_ORDER_CANCELLED, true); // Durable queue
    }

    @Bean
    public Binding bindingAdminOrderCreated(TopicExchange exchange) {
        return BindingBuilder.bind(adminOrderCreatedQueue()).to(exchange).with(ROUTING_KEY_ADMIN_ORDER_CREATED);
    }

    @Bean
    public Binding bindingAdminOrderCancelled(TopicExchange exchange) {
        return BindingBuilder.bind(adminOrderCancelledQueue()).to(exchange).with(ROUTING_KEY_ADMIN_ORDER_CANCELLED);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }
}

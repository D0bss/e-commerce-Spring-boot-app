package com.example.checkouttransactionservice.config;

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

@Configuration
public class RabbitMQConfig {

    // Exchange name
    public static final String EXCHANGE_NAME = "checkout.events.exchange";

    // Queue names for regular events
    public static final String QUEUE_ORDER_CREATED = "order.created.queue";
    public static final String QUEUE_ORDER_PAID = "order.paid.queue";
    public static final String QUEUE_ORDER_CANCELLED = "order.cancelled.queue";

    // Queue names for admin notifications
    public static final String QUEUE_ADMIN_ORDER_CREATED = "admin.order.created.queue";
    public static final String QUEUE_ADMIN_ORDER_CANCELLED = "admin.order.cancelled.queue";

    // Routing keys for regular events
    public static final String ROUTING_KEY_ORDER_CREATED = "order.created";
    public static final String ROUTING_KEY_ORDER_PAID = "order.paid";
    public static final String ROUTING_KEY_ORDER_CANCELLED = "order.cancelled";

    // Routing keys for admin notifications
    public static final String ROUTING_KEY_ADMIN_ORDER_CREATED = "admin.order.created";
    public static final String ROUTING_KEY_ADMIN_ORDER_CANCELLED = "admin.order.cancelled";

    @Bean
    public TopicExchange checkoutEventsExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    // Regular event queues
    @Bean
    public Queue orderCreatedQueue() {
        return new Queue(QUEUE_ORDER_CREATED, true); // Durable queue
    }

    @Bean
    public Queue orderPaidQueue() {
        return new Queue(QUEUE_ORDER_PAID, true); // Durable queue
    }

    @Bean
    public Queue orderCancelledQueue() {
        return new Queue(QUEUE_ORDER_CANCELLED, true); // Durable queue
    }

    // Admin notification queues
    @Bean
    public Queue adminOrderCreatedQueue() {
        return new Queue(QUEUE_ADMIN_ORDER_CREATED, true); // Durable queue
    }

    @Bean
    public Queue adminOrderCancelledQueue() {
        return new Queue(QUEUE_ADMIN_ORDER_CANCELLED, true); // Durable queue
    }

    // Bindings for regular events
    @Bean
    public Binding bindingOrderCreated(TopicExchange exchange) {
        return BindingBuilder.bind(orderCreatedQueue()).to(exchange).with(ROUTING_KEY_ORDER_CREATED);
    }

    @Bean
    public Binding bindingOrderPaid(TopicExchange exchange) {
        return BindingBuilder.bind(orderPaidQueue()).to(exchange).with(ROUTING_KEY_ORDER_PAID);
    }

    @Bean
    public Binding bindingOrderCancelled(TopicExchange exchange) {
        return BindingBuilder.bind(orderCancelledQueue()).to(exchange).with(ROUTING_KEY_ORDER_CANCELLED);
    }

    // Bindings for admin notifications
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

package com.example.userapp.config;

import com.example.userapp.model.Session;
import com.example.userapp.service.SessionManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

@Component
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Session> sessionRedisTemplate(
            RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, Session> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(RedisSerializer.string());
        template.setHashKeySerializer(RedisSerializer.string());
        template.setValueSerializer(sessionRedisSerializer());
        template.setHashValueSerializer(sessionRedisSerializer());

        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public SessionManager sessionManager(RedisTemplate<String, Session> sessionRedisTemplate) {
        SessionManager manager = SessionManager.getInstance();
        manager.setRedisTemplate(sessionRedisTemplate);
        return manager;
    }

    @Bean
    public GenericJackson2JsonRedisSerializer sessionRedisSerializer() {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);

        return new GenericJackson2JsonRedisSerializer(objectMapper);
    }
}
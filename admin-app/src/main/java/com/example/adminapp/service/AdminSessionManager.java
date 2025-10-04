package com.example.adminapp.service;

import com.example.adminapp.model.AdminSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AdminSessionManager {

    private final RedisTemplate<String, AdminSession> redisTemplate;

    @Autowired
    public AdminSessionManager(RedisTemplate<String, AdminSession> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void startSession(AdminSession session) {
        redisTemplate.opsForValue().set(session.getToken(), session);
    }

    public void endSession(String token) {
        if (token != null) {
            redisTemplate.delete(token);
        } else {
            System.out.println("Token is null. Cannot delete session.");
        }
    }

    public AdminSession getSession(String token) {
        return redisTemplate.opsForValue().get(token);
    }

    public boolean isValid(String token) {
        if (token == null) {
            System.out.println("Token is null");
            return false;
        }
        AdminSession session = redisTemplate.opsForValue().get(token);
        if (session == null) {
            System.out.println("No session found for token");
            return false;
        }
        if (session.getExpiry() == null) {
            System.out.println("Session expiry is null");
            return false;
        }
        return session.getExpiry().isAfter(LocalDateTime.now());
    }
}

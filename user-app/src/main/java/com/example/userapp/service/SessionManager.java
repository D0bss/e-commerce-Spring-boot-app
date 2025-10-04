package com.example.userapp.service;

import com.example.userapp.model.Session;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.time.LocalDateTime;

public class SessionManager {

    private static SessionManager instance;
    private RedisTemplate<String, Session> redisTemplate;

    // Private constructor
    private SessionManager() {}

    // Thread-safe Singleton getter
    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    // Setter to inject RedisTemplate after creation
    public void setRedisTemplate(RedisTemplate<String, Session> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void startSession(Session session) {
        // Store the session in Redis with a TTL (time-to-live) of 1 hour
        redisTemplate.opsForValue().set("session:" + session.getToken(), session, Duration.ofHours(2));
    }

    public void endSession(String token) {
        redisTemplate.delete("session:" + token);
    }

    public Session getSession(String token) {
        return redisTemplate.opsForValue().get("session:" + token);
    }

//    public boolean isValid(String authHeader) {
//        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            return false;
//        }
//        String token = authHeader.substring(7); // Remove "Bearer "
//        return redisTemplate.hasKey("session:" + token); // Check Redis
//    }
public boolean isValid(String token) {
    if (token == null || token.isEmpty()) {
        return false;
    }
    return redisTemplate.hasKey("session:" + token);
}
    public Long getUserIdFromToken(String token) {
        Session session = getSession(token);
        return (session != null) ? session.getUserId() : null;
    }


}

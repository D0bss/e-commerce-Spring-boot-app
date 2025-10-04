package com.example.adminapp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;


public class AdminSession implements Serializable {
    private String token;
    private String adminId;
    private LocalDateTime expiry;

    public AdminSession() {}
    public AdminSession(String token, String adminId, LocalDateTime expiry) {
        this.token = token;
        this.adminId = adminId;
        this.expiry = expiry;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public LocalDateTime getExpiry() {
        return expiry;
    }

    public void setExpiry(LocalDateTime expiry) {
        this.expiry = expiry;
    }
}

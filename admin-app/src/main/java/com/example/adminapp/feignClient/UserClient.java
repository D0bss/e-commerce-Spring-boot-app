package com.example.adminapp.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-app",url = "http://localhost:8081")
public interface UserClient {
    @DeleteMapping("/internal/users/{id}")
    void deleteUser(@PathVariable("id") Long userId);

}

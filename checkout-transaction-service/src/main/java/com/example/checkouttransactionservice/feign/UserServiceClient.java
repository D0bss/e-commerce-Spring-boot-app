package com.example.checkouttransactionservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Map;
import java.util.List;

@FeignClient(name = "user-app", url = "http://host.docker.internal:8081")
public interface UserServiceClient {

    @GetMapping("/users/{userId}") 
    Map<String, Object> getUserById(@PathVariable("userId") Long userId);
    
    // Token validation method
    @GetMapping("/users/validate-token")
    boolean isValidToken(@RequestParam("token") String token);
    
    // Get user by token
    @GetMapping("/users/by-token")
    Map<String, Object> getUserByToken(@RequestParam("token") String token);
    
    // Get user's product list by token
    @GetMapping("/users/products")
    List<String> getUserProductIds(@RequestParam("token") String token);
}

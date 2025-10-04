package com.example.userapp.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "checkout-transaction-service", url = "http://host.docker.internal:8083")
public interface TransactionServiceClient {

    @PostMapping("/transactions/checkout")
    void checkout(@RequestParam("userId") Long userId,
                  @RequestBody List<String> productIds);


    @PostMapping("/transactions/cancel")
    void cancel(@RequestParam Long userId);


}

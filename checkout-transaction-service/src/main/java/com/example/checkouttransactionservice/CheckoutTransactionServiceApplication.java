package com.example.checkouttransactionservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class CheckoutTransactionServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(CheckoutTransactionServiceApplication.class, args);
    }
}

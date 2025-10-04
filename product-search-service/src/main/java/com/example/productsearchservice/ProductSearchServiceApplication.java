package com.example.productsearchservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
//@ComponentScan(basePackages = {"com.example.productsearchservice.service", "com.example.productsearchservice.strategy", "com.example.productsearchservice.repository"})

@EnableFeignClients

public class ProductSearchServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductSearchServiceApplication.class, args);
    }
}

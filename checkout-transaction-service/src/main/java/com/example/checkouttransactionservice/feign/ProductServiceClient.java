package com.example.checkouttransactionservice.feign;

// import com.example.checkouttransactionservice.dto.ProductDTO; // DTO is removed
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Map;

// Assuming the Product Service is running at localhost:8082 as per user-app's ProductServiceClient
// The name "product-service" should match the service discovery name if Eureka/Consul is used.
// For direct URL, ensure it's configurable in application.properties.
@FeignClient(name = "product-service", url = "http://host.docker.internal:8082")
public interface ProductServiceClient {

    // Assuming an endpoint like /products/{id} exists in the Product Service to fetch product details
    // Now returns a Map instead of ProductDTO
    @GetMapping("/products/get/{productId}")
    Map<String, Object> getProductById(@PathVariable("productId") String productId);

    // We can also include the isValidProduct method if needed, though fetching details might imply validity
    // @GetMapping("/products/exists")
    // boolean isValidProduct(@RequestParam("productId") Long productId);
}


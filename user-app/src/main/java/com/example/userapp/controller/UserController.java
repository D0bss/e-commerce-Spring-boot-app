package com.example.userapp.controller;

import com.example.userapp.model.User;

import com.example.userapp.exception.UserNotFoundException;
import com.example.userapp.exception.ValidationException;
import com.example.userapp.service.EmailService;
import com.example.userapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
//@ComponentScan(basePackages = "com.example.userapp")



public class UserController {

    private final UserService userService;
    private final EmailService emailService;

    @PostMapping("/register")
    public String register(@RequestParam String email,
                           @RequestParam String password,
                           @RequestParam String name,
                           @RequestParam(required = false) String address,
                           @RequestParam(required = false) String preferences) {
        return userService.register(email, password, name, address, preferences);
    }


//    @GetMapping("/validate")
//    public String validateEmail(@RequestParam String email) {
//        return userService.validateEmail(email);
//    }

    // GET /users/{id} - Read user by ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    // PUT /users/{id} - Update user details
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable("id") Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String preferences
    ) {
        User updatedUser = userService.updateUser(id, name, address, preferences);
        return ResponseEntity.ok(updatedUser);
    }


    @GetMapping("/send-verification")
    public ResponseEntity<String> sendVerification(@RequestParam String email) {
        try {
            String result = userService.sendEmailVerification(email);
            return ResponseEntity.ok(result);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found: " + e.getMessage());
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid verification link: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error: " + e.getMessage());
        }
    }


    @GetMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        try {
            String result = userService.confirmEmailVerification(token);
            return ResponseEntity.ok(result); // 200 OK response
        } catch (ValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid or expired verification link: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal server error: " + e.getMessage());
        }
    }


    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestParam String email,
            @RequestParam String password
    ) {
        Map<String, Object> response = userService.login(email, password);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        userService.logout(token.replace("Bearer ", ""));
        return ResponseEntity.ok("Logged out successfully.");
    }


//    @PostMapping("/cart/add")
//    public void addProduct(@RequestHeader("Authorization") String token,
//                           @RequestParam Long productId) {
//        userService.addProduct(token, productId);
//    }

    @PostMapping("/checkout")
public ResponseEntity<Map<String, Object>> checkout(@RequestHeader("Authorization") String token) {
    userService.checkout(token);
    
    Map<String, Object> response = new HashMap<>();
    response.put("status", "success");
    response.put("message", "Checkout initiated successfully");
    
    return ResponseEntity.ok(response);
}

    @PostMapping("/cancel")
    public ResponseEntity<Map<String, Object>> cancel(@RequestHeader("Authorization") String token) {
        userService.cancelCheckout(token);
        Map<String, Object> response = new HashMap<>();
    response.put("status", "success");
    response.put("message", "Checkout canceled successfully");
    
    return ResponseEntity.ok(response);
}
    

    @GetMapping("/validateSession")
    public ResponseEntity<String> secureEndpoint(@RequestHeader("Authorization") String authHeader) {
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid Authorization header");
            }

            String token = authHeader.replace("Bearer ", "");
            userService.validateSession(token);  //  this line likely throws the error
            return ResponseEntity.ok("Access granted");
        } catch (Exception ex) {
            ex.printStackTrace(); // Log the actual error to the console
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + ex.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }

    @PostMapping("/products")
    public ResponseEntity<String> addProductToUserList(
            @RequestHeader("Authorization") String token,
            @RequestParam String productId) {
        userService.addProductToList(token, productId);
        return ResponseEntity.ok("Product added to user list.");
    }


//
//    @FeignClient(name = "product-search-service", url = "http://host.docker.internal:8082")
//    public interface ProductServiceClient {
//
//        @GetMapping("/products/exists")
//        boolean isValidProduct(@RequestParam("productId") String productId);
//
//        @PostMapping("/products/batch")
//        ResponseEntity<List<Map<String, Object>>> getProductsByIds(@RequestBody List<String> ids);
//
//    }

    @GetMapping("/products")
    public ResponseEntity<List<Map<String, Object>>> getUserProducts(@RequestHeader("Authorization") String token) {
        List<Map<String, Object>> products = userService.getUserProductList(token);
        return ResponseEntity.ok(products);
    }



}




package com.example.userapp.service;

import com.example.userapp.exception.UnauthorizedException;
import com.example.userapp.exception.UserNotFoundException;
import com.example.userapp.exception.ValidationException;
import com.example.userapp.feign.ProductServiceClient;
import com.example.userapp.feign.TransactionServiceClient;
import com.example.userapp.model.Session;
import com.example.userapp.model.User;
import com.example.userapp.model.UserProductList;
import com.example.userapp.repository.UserProductListRepository;
import com.example.userapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;


import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {


    private final UserRepository userRepository;
    private final UserProductListRepository listRepository;
    private final ProductServiceClient productClient;
    private final TransactionServiceClient transactionClient;
    private final SessionManager sessionManager; // Now injected as a singleton
    private final EmailService emailService;
    private final RedisTemplate<String, String> stringRedisTemplate;



    public String register(String email, String password, String name, String address, String preferences) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new ValidationException("Email already registered.");
        }

        User user = new User.UserBuilder(email, password, name)
                .address(address)
                .preferences(preferences)
                .build();

        userRepository.save(user);
        return "Registration successful. Please validate your email.";
    }
    ///  Aact as a read method

    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));
    }

    // act as update
    public User updateUser(Long userId, String name, String address, String preferences) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + userId));

        if (name != null) user.setName(name);
        if (address != null) user.setAddress(address);
        if (preferences != null) user.setPreferences(preferences);

        return userRepository.save(user);
    }



    public String validateEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.validateEmail();
        userRepository.save(user);
        return "Email validated.";
    }



    public String sendEmailVerification(String email) {
        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UserNotFoundException("User not found"));

            if (user.isValidated()) {
                return "Email already validated.";
            }

            // Generate token and store in Redis
            String token = UUID.randomUUID().toString();
            stringRedisTemplate.opsForValue().set("verify:" + token, email, Duration.ofHours(3));

            // Build the verification link
            //String link = "http://127.0.0.1:8081/users/verify-email?token=" + token;
            String link;
            String host = System.getenv("HOST_NAME");

            if (host != null && !host.isEmpty()) {
                link = "http://" + host + ":8081/users/verify-email?token=" + token;
            } else {
                // Fallback to default
                link = "http://127.0.0.1:8081/users/verify-email?token=" + token;
            }

            // Send the email verification
            emailService.send(email, "Email Verification", "Click here to verify: " + link);

            return "Verification email sent.";
        } catch (UserNotFoundException e) {
            // Handle UserNotFoundException
            return "User not found: " + e.getMessage();
        } catch (Exception e) {
            // Handle other general exceptions
            return "An unexpected error occurred: " + e.getMessage();
        }
    }

//
//    public String sendEmailVerification(String email) {
//        try {
//            User user = userRepository.findByEmail(email)
//                    .orElseThrow(() -> new UserNotFoundException("User not found"));
//
//            if (user.isValidated()) {
//                return "Email already validated.";
//            }
//
//            // Generate token and store in Redis
//            String token = UUID.randomUUID().toString();
//            stringRedisTemplate.opsForValue().set("verify:" + token, email, Duration.ofHours(3));
//
//            // Build the verification link
//            String link;
//            String host = System.getenv("HOST_NAME");  // Get host dynamically
//
//            if (host != null && !host.isEmpty()) {
//                link = "http://" + host + ":8081/users/verify-email?token=" + token;
//            } else {
//                // Fallback to default local URL
//                link = "http://localhost:8081/users/verify-email?token=" + token;
//            }
//
//            // Send the email verification
//            emailService.send(email, "Email Verification", "Click here to verify: " + link);
//
//            return "Verification email sent.";
//        } catch (UserNotFoundException e) {
//            // Handle UserNotFoundException
//            return "User not found: " + e.getMessage();
//        } catch (Exception e) {
//            // Handle other general exceptions
//            return "An unexpected error occurred: " + e.getMessage();
//        }
//    }


    public String confirmEmailVerification(String token) {
        String email = stringRedisTemplate.opsForValue().get("verify:" + token);
        if (email == null) {
            throw new ValidationException("Invalid or expired verification link.");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.validateEmail();
        userRepository.save(user);
        stringRedisTemplate.delete("verify:" + token);

        return "Email validated successfully.";
    }



    public Map<String, Object> login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!user.getPassword().equals(password)) throw new UnauthorizedException("Invalid credentials.");
        if (!user.isValidated()) throw new UnauthorizedException("Email not validated.");

        String token = UUID.randomUUID().toString();
        Session session = new Session(token, user.getId(), LocalDateTime.now().plusHours(2),user.getEmail());
        System.out.println("Calling sessionManager.startSession()");
        sessionManager.startSession(session); // Using singleton instance

        Map<String, Object> response = new HashMap<>();
        response.put("userId", user.getId());
        response.put("name", user.getName());
        response.put("token", token);
        response.put("email", user.getEmail());
        return response;
    }

    public void logout(String token) {
        sessionManager.endSession(token);
    }

//    public void addProduct(String token, Long productId) {
//        Session session = validateSession(token);
//        Long userId = session.getUserId();
//
//        if (!productClient.isValidProduct(productId)) {
//            throw new ValidationException("Invalid product ID.");
//        }
//
//        UserProductList list = listRepository.findById(userId).orElse(
//                UserProductList.builder().userId(userId).productIds(new ArrayList<>()).build()
//        );
//
//        list.getProductIds().add(productId);
//        listRepository.save(list);
//    }

    private final ProductServiceClient productServiceClient;
    private final UserProductListRepository productListRepository;
    private final RedisTemplate<String, Session> redisTemplate;

    public void addProductToList(String token, String productId) {
        SessionManager sessionManager = SessionManager.getInstance();
        sessionManager.setRedisTemplate(redisTemplate);

        if (!sessionManager.isValid(token)) {
            throw new IllegalStateException("Invalid or expired session.");
        }

        Session session = sessionManager.getSession(token);
        Long userId = session.getUserId();

        // Validate product existence via Feign client
        boolean productExists = productServiceClient.isValidProduct(productId);
        if (!productExists) {
            throw new IllegalArgumentException("Invalid product ID: " + productId);
        }

        // Fetch user from DB
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        // Fetch existing user product list or create new
        List<UserProductList> userProductLists = productListRepository.findByUser(user);
        UserProductList list;
        if (userProductLists.isEmpty()) {
            // Create new list with the productId
            list = new UserProductList();
            list.setUser(user);
            list.setProductIds(new ArrayList<>());
        } else {
            // Use existing list
            list = userProductLists.get(0);
            if (list.getProductIds() == null) {
                list.setProductIds(new ArrayList<>());
            }
        }

        // Add productId only if not already in the list
        if (!list.getProductIds().contains(productId)) {
            list.getProductIds().add(productId);
        }

        // Save updated list
        productListRepository.save(list);
    }



    public void checkout(String token) {
        Session session = validateSession(token);
        Long userId = session.getUserId();

        UserProductList list = listRepository.findById(userId)
                .orElseThrow(() -> new ValidationException("Your list is empty."));

        List<String> productIds = list.getProductIds();
        if (productIds == null || productIds.isEmpty()) {
            throw new ValidationException("Your list is empty.");
        }

        // Send to transaction microservice
        transactionClient.checkout(userId, productIds);

        // Clear the list after checkout
        list.setProductIds(new ArrayList<>());
        listRepository.save(list);
    }


    public void cancelCheckout(String token) {
        Session session = validateSession(token);
        transactionClient.cancel(session.getUserId());
    }
    public Session validateSession(String token) {
        if (!sessionManager.isValid(token)) {
            throw new UnauthorizedException("Invalid or expired session.");
        }
        return sessionManager.getSession(token); // Safe to retrieve again
    }


    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<Map<String, Object>> getUserProductList(String token) {
        Session session = validateSession(token);
        Long userId = session.getUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        List<UserProductList> lists = productListRepository.findByUser(user);
        if (lists.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> productIds = lists.get(0).getProductIds();

        // Call product microservice using Feign client
        ResponseEntity<List<Map<String, Object>>> response = productServiceClient.getProductsByIds(productIds);

        // Return the body or empty list if null
        return response.getBody() != null ? response.getBody() : Collections.emptyList();
    }



    public void deleteUserById(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User not found");
        }
        userRepository.deleteById(userId);


    }
//    private final UserProductListRepository userProductListRepository;
//
//    public void checkout(String token) {
//        if (!sessionManager.isValid(token)) {
//            throw new IllegalStateException("User session is invalid or expired.");
//        }
//
//        Long userId = sessionManager.getUserIdFromToken(token);
//        if (userId == null) {
//            throw new IllegalStateException("User session does not contain a valid user ID.");
//        }
//
//        Optional<UserProductList> optional = userProductListRepository.findByUserId(userId);
//        if (optional.isEmpty() || optional.get().getProductIds().isEmpty()) {
//            throw new IllegalStateException("User product list is empty or does not exist.");
//        }
//
//        List<Long> productIds = optional.get().getProductIds();
//
//        transactionClient.createCheckout(userId, productIds);
//
//        // Optionally clear product list after successful checkout
//        optional.get().getProductIds().clear();
//        userProductListRepository.save(optional.get());
//    }

}
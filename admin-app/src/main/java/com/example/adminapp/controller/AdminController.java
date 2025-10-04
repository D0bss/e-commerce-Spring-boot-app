package com.example.adminapp.controller;

import com.example.adminapp.adminRequest.AdminRequest;
import com.example.adminapp.model.Admin;
import com.example.adminapp.model.AdminSession;
import com.example.adminapp.model.Promotion;
import com.example.adminapp.service.AdminServiceImpl;
import com.example.adminapp.service.AdminSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    //private String token;

    private final AdminServiceImpl adminService;
    private final AdminSessionManager adminSessionManager;

    @Autowired
    public AdminController(AdminServiceImpl adminService , AdminSessionManager adminSessionManager) {
        this.adminService = adminService;
        this.adminSessionManager = adminSessionManager;
    }

    //private boolean isAdminLoggedIn(String token) {
       // return adminSessionManager.isValid(token);
    //}

    @GetMapping("/login")
    public ResponseEntity<String> login(@RequestParam String email, @RequestParam String password) {
        Admin admin = adminService.getAdminByEmail(email);

        if (admin != null && admin.getPassword().equals(password)) {
            String token = UUID.randomUUID().toString();
            LocalDateTime expiry = LocalDateTime.now().plusHours(1);
            AdminSession session = new AdminSession(token, admin.getId(), expiry);
            adminSessionManager.startSession(session);
            return ResponseEntity.ok(token);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        if (!adminSessionManager.isValid(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired session");
        }
        adminSessionManager.endSession(token);
        return ResponseEntity.ok("Admin logged out successfully");
    }



    @PostMapping("/createAdmin")
    public ResponseEntity<Admin> createAdmin(@RequestHeader("Authorization") String token , @RequestBody AdminRequest request) {
        if (!adminSessionManager.isValid(token)) {
           throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        }
        Admin createdAdmin = adminService.createAdmin(request.getEmail(), request.getPassword());
        return ResponseEntity.status(HttpStatus.CREATED).body(createdAdmin);
    }

    @GetMapping("/getAdminByEmail/{email}")
    public ResponseEntity<Admin> getAdminByEmail(@RequestHeader("Authorization") String token, @PathVariable String email) {
        if (!adminSessionManager.isValid(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        }
        Admin admin = adminService.getAdminByEmail(email);
        if (admin == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin not found");
        }
        return ResponseEntity.ok(admin);
    }

    @GetMapping("/getAllAdmins")
    public ResponseEntity<List<Admin>> getAllAdmins(@RequestHeader("Authorization") String token) {
        if (!adminSessionManager.isValid(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        }
        List<Admin> admins = adminService.getAllAdmins();
        return ResponseEntity.ok(admins);
    }

    @DeleteMapping("/deleteAdmin/{email}")
    public ResponseEntity<String> deleteAdmin(@RequestHeader("Authorization") String token,     @PathVariable String email) {
        if (!adminSessionManager.isValid(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        }
        String result = adminService.deleteAdmin(email);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/promotions")
    public ResponseEntity<Promotion> createPromotion(@RequestHeader("Authorization") String token, @RequestBody Promotion promotion) {
        if (!adminSessionManager.isValid(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        }
        Promotion createdPromotion = adminService.createPromotionAsAdmin(promotion);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPromotion);
    }
    //@PostMapping("/applyPromotion")
   // public double applyPromotion(@RequestParam String promoId, @RequestParam double originalPrice) {
    //    if (!isAdminLoggedIn(token)) {
    //        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
    //    }
      //  return adminService.applyPromotionAsAdmin(promoId, originalPrice);
  //  }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        if (!adminSessionManager.isValid(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        }
        adminService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully via Feign");
    }

    @PutMapping("/products/{productId}/apply-promotion/{promotionId}")
    public ResponseEntity<String> applyPromotionToProduct(@RequestHeader("Authorization") String token, @PathVariable String productId, @PathVariable String promotionId) {
        if (!adminSessionManager.isValid(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        }
        adminService.applyPromotionToProduct(productId, promotionId);
        return ResponseEntity.ok("Promotion applied and price updated.");
    }



}

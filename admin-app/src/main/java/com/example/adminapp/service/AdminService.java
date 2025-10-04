package com.example.adminapp.service;
import com.example.adminapp.model.Admin;
import com.example.adminapp.model.Promotion;

import java.util.List;

public interface AdminService {
    Admin createAdmin(String email, String password);
    Admin getAdminByEmail(String email);
    List<Admin> getAllAdmins();
    String deleteAdmin(String email);
    boolean validateLogin(String email, String password);
    Promotion createPromotionAsAdmin(Promotion promotion);
    Double applyPromotionAsAdmin(String promoId, double originalPrice);
    void deleteUser(Long userId);
    void applyPromotionToProduct(String productId, String promoId);


}

package com.example.adminapp.service;

import com.example.adminapp.feignClient.ProductClient;
import com.example.adminapp.feignClient.UserClient;
import com.example.adminapp.model.Admin;
import com.example.adminapp.model.Promotion;
import com.example.adminapp.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final PromotionService promotionService;
    private final UserClient userClient;
    private final ProductClient productClient ;

    @Autowired
    public AdminServiceImpl(AdminRepository adminRepository,
                            PromotionService promotionService,
                            UserClient userClient,ProductClient productClient) {
        this.adminRepository = adminRepository;
        this.promotionService = promotionService;
        this.userClient = userClient;
        this.productClient = productClient;
    }

    @Override
    public Admin createAdmin(String email, String password) {
        Admin admin = new Admin(email, password);
        return adminRepository.save(admin);
    }

    @Override
    @Cacheable(value = "admins" , key = "#email")
    public Admin getAdminByEmail(String email) {
        return adminRepository.findByEmail(email);
    }

    @Override
    public List<Admin> getAllAdmins() {
        return adminRepository.findAll();
    }

    @Override
    public String deleteAdmin(String email) {
        Admin admin = adminRepository.findByEmail(email);
        if (admin != null) {
            adminRepository.delete(admin);
            return "Admin deleted";
        }
        return "Admin not found";
    }

    @Override
    public boolean validateLogin(String email, String password) {
        Admin admin = adminRepository.findByEmail(email);
        return admin != null && admin.getPassword().equals(password);
    }

    @Override
    public Promotion createPromotionAsAdmin(Promotion promotion) {
        return promotionService.createPromotion(promotion);
    }

    @Override
    public Double applyPromotionAsAdmin(String promoId, double originalPrice) {
        return promotionService.applyDiscount(promoId, originalPrice);
    }

    @Override
    public void deleteUser(Long userId) {
        userClient.deleteUser(userId);
    }

    @Override
    public void applyPromotionToProduct(String productId, String promoId) {
        double originalPrice = productClient.getProductPrice(productId);
        double discountedPrice = promotionService.applyDiscount(promoId, originalPrice);
        productClient.updateProductPrice(productId, discountedPrice);
    }


}

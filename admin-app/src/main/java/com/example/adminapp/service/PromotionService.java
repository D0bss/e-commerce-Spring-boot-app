package com.example.adminapp.service;
import com.example.adminapp.model.Promotion;
import java.util.List;

public interface PromotionService {
    Promotion createPromotion(Promotion promotion);
    Promotion getPromotionById(String id);
    List<Promotion> getAllPromotions();
    Promotion updatePromotion(String id, Promotion updatedPromotion);
    void deletePromotion(String id);
    double applyDiscount(String promoId, double originalPrice);
}

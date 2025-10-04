package com.example.adminapp.service;



import com.example.adminapp.model.Promotion;
import com.example.adminapp.repository.PromotionRepository;
import com.example.adminapp.strategy.DiscountStrategy;
import com.example.adminapp.strategy.DiscountStrategyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service // Singleton by default
public class PromotionServiceImpl implements PromotionService {

    private final PromotionRepository promotionRepository;

    @Autowired
    public PromotionServiceImpl(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    @Override
    public Promotion createPromotion(Promotion promotion) {
        if (promotionRepository.existsByDescription(promotion.getDescription())) {
            throw new IllegalArgumentException("Promotion already exists with that description.");
        }
        return promotionRepository.save(promotion);
    }

    @Override
    public Promotion getPromotionById(String id) {
        return promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promotion not found"));
    }

    @Override
    public List<Promotion> getAllPromotions() {
        return promotionRepository.findAll();
    }

    @Override
    public Promotion updatePromotion(String id, Promotion updatedPromotion) {
        Promotion existing = getPromotionById(id);
        existing.setDescription(updatedPromotion.getDescription());
        existing.setDiscountType(updatedPromotion.getDiscountType());
        existing.setValue(updatedPromotion.getValue());
        existing.setValidFrom(updatedPromotion.getValidFrom());
        existing.setValidTo(updatedPromotion.getValidTo());
        existing.setActive(updatedPromotion.isActive());
        return promotionRepository.save(existing);
    }

    @Override
    public void deletePromotion(String id) {
        promotionRepository.deleteById(id);
    }

    @Override
    public double applyDiscount(String promoId, double originalPrice) {
        Promotion promo = getPromotionById(promoId);
        DiscountStrategy strategy = DiscountStrategyFactory.getStrategy(promo.getDiscountType());
        return strategy.apply(originalPrice, promo.getValue());
    }
}


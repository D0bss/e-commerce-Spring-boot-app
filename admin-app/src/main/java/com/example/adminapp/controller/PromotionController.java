package com.example.adminapp.controller;
import com.example.adminapp.model.Promotion;
import com.example.adminapp.service.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/promotions")
public class PromotionController {

    @Autowired
    private PromotionService promotionService;

    @PostMapping
    public Promotion create(@RequestBody Promotion promotion) {
        return promotionService.createPromotion(promotion);
    }

    @GetMapping
    public List<Promotion> getAll() {
        return promotionService.getAllPromotions();
    }

    @GetMapping("/{id}")
    public Promotion getById(@PathVariable String id) {
        return promotionService.getPromotionById(id);
    }

    @PutMapping("/{id}")
    public Promotion update(@PathVariable String id, @RequestBody Promotion promotion) {
        return promotionService.updatePromotion(id, promotion);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        promotionService.deletePromotion(id);
    }

    @GetMapping("/{id}/apply")
    public double applyDiscount(@PathVariable String id, @RequestParam double price) {
        return promotionService.applyDiscount(id, price);
    }
}


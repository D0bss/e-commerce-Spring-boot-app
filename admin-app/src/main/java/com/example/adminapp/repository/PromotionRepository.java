package com.example.adminapp.repository;

import com.example.adminapp.model.Promotion;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PromotionRepository extends MongoRepository<Promotion, String> {
    boolean existsByDescription(String description);
}

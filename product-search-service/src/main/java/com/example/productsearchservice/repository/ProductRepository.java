package com.example.productsearchservice.repository;

import com.example.productsearchservice.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product> findByCategoryContainingIgnoreCase(String category);

    @Query("{'$or': [{'name': {$regex: ?0, $options: 'i'}}, {'category': {$regex: ?1, $options: 'i'}}]}")
    List<Product> findByNameOrCategory(String name, String category);

    @Query("{'$and': [" +
            "{'$or': [" +
            "  {'name': {$regex: ?0, $options: 'i'}}," +
            "  {'category': {$regex: ?1, $options: 'i'}}" +
            "]}," +
            "{'$or': [" +
            "  {'price': {$gte: ?2, $lte: ?3}}," +
            "  {?2: null}," +
            "  {?3: null}" +
            "]}," +
            "{'$or': [" +
            "  {'rating': {$gte: ?4}}," +
            "  {?4: null}" +
            "]}," +
            "{'$or': [" +
            "  {'available': ?5}," +
            "  {?5: null}" +
            "]}," +
            "]}")
    List<Product> findByNameOrCategoryWithFilters(
            String name, String category,
            Double minPrice, Double maxPrice,
            Double minRating,
            Boolean available);

    // Standalone filter methods
    List<Product> findByPriceBetween(Double minPrice, Double maxPrice);
    List<Product> findByRatingGreaterThanEqual(Double minRating);
    List<Product> findByAvailable(Boolean available);

    @Query("{'$and': [" +
            "{'price': {$gte: ?0, $lte: ?1}}," +
            "{'rating': {$gte: ?2}}," +
            "{'available': ?3}" +
            "]}")
    List<Product> findByFilters(
            Double minPrice, Double maxPrice,
            Double minRating,
            Boolean available);
}
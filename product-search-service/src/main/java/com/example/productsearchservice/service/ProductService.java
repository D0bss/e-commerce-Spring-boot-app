package com.example.productsearchservice.service;

import com.example.productsearchservice.dto.ProductDTO;
import com.example.productsearchservice.model.Product;
import com.example.productsearchservice.repository.ProductRepository;
import com.example.productsearchservice.strategy.ProductCreationFactory;
import com.example.productsearchservice.strategy.ProductCreationStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository repository;
    private final ProductCreationFactory factory;

    @Autowired
    public ProductService(ProductRepository repository, ProductCreationFactory factory) {
        this.repository = repository;
        this.factory = factory;
        System.out.println(">>> ProductService constructor hit!" + this.factory);
    }


    // Create
    public Product createProduct(ProductDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Product data cannot be null");
        }
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }
        if (dto.getPrice() < 0) {
            throw new IllegalArgumentException("Product price cannot be negative");
        }
        if (dto.getRating() < 0 || dto.getRating() > 5) {
            throw new IllegalArgumentException("Product rating must be between 0 and 5");
        }

        // Determine the product type and get the appropriate strategy
        String type = dto.getType() == null  ? "basic" : dto.getType().toLowerCase();
        ProductCreationStrategy strategy = factory.getStrategy(type);
        Product product = strategy.createProduct(dto);
        return repository.save(product);
    }

    // Read
    public List<Product> getAllProducts() {
        return repository.findAll();
    }

    public Product getProductById(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be empty");
        }
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));
    }

    // Update
    public Product updateProduct(String id, ProductDTO dto) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be empty");
        }
        if (dto == null) {
            throw new IllegalArgumentException("Product data cannot be null");
        }

        Product existing = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));

        // Validate update data
        if (dto.getName() != null && !dto.getName().trim().isEmpty()) {
            existing.setName(dto.getName());
        }
        if (dto.getCategory() != null) {
            existing.setCategory(dto.getCategory());
        }
        if (dto.getPrice() >= 0) {
            existing.setPrice(dto.getPrice());
        }
        if (dto.getRating() >= 0 && dto.getRating() <= 5) {
            existing.setRating(dto.getRating());
        }
        existing.setAvailable(dto.isAvailable());

        return repository.save(existing);
    }

    // Delete
    public void deleteProduct(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Product ID cannot be empty");
        }
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Product not found with id: " + id);
        }
        repository.deleteById(id);
    }

    // Search methods
    public List<Product> searchByName(String name) {
        return repository.findByNameContainingIgnoreCase(name);
    }

    public List<Product> searchByCategory(String category) {
        return repository.findByCategoryContainingIgnoreCase(category);
    }

    public List<Product> searchByNameAndCategory(String name, String category) {
        return repository.findByNameOrCategory(name, category);
    }

    public List<Product> searchWithFilters(
            String name,
            String category,
            Double minPrice,
            Double maxPrice,
            Double minRating,
            Boolean available) {
        
        minPrice = minPrice != null ? minPrice : 0.0;
        maxPrice = maxPrice != null ? maxPrice : Double.MAX_VALUE;
        minRating = minRating != null ? minRating : 0.0;
        
        return repository.findByNameOrCategoryWithFilters(
                name != null ? name : "",
                category != null ? category : "",
                minPrice,
                maxPrice,
                minRating,
                available
        );
    }

    // Filter methods
    public List<Product> filterByPriceRange(Double minPrice, Double maxPrice) {
        minPrice = minPrice != null ? minPrice : 0.0;
        maxPrice = maxPrice != null ? maxPrice : Double.MAX_VALUE;
        return repository.findByPriceBetween(minPrice, maxPrice);
    }

    public List<Product> filterByRating(Double minRating) {
        minRating = minRating != null ? minRating : 0.0;
        return repository.findByRatingGreaterThanEqual(minRating);
    }

    public List<Product> filterByAvailability(Boolean available) {
        return repository.findByAvailable(available);
    }

    public List<Product> filterByAll(
            Double minPrice,
            Double maxPrice,
            Double minRating,
            Boolean available) {
        
        minPrice = minPrice != null ? minPrice : 0.0;
        maxPrice = maxPrice != null ? maxPrice : Double.MAX_VALUE;
        minRating = minRating != null ? minRating : 0.0;
        
        return repository.findByFilters(minPrice, maxPrice, minRating, available);

    }

    public void updatePrice(String productId, double newPrice) {
        Product product = getProductById(productId); // reuse existing method
        product.setPrice(newPrice);
        repository.save(product);
    }

    public double getPrice(String productId) {
        Product product = getProductById(productId); // reuse existing method
        return product.getPrice();
    }


}

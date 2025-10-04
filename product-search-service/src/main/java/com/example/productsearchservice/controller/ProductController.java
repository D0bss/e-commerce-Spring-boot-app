package com.example.productsearchservice.controller;

import com.example.productsearchservice.dto.ProductDTO;
import com.example.productsearchservice.model.Product;
import com.example.productsearchservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService service;

    @PostMapping             // TESTED
    public ResponseEntity<?> create(@RequestBody ProductDTO dto) {
        try {
            Product product = service.createProduct(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(product);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/exists")
    public ResponseEntity<Boolean> isValidProduct(@RequestParam String productId) {
        try {
            Product product = service.getProductById(productId);
            return ResponseEntity.ok(true);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(false);
        }
    }

    @GetMapping       //tested
    public ResponseEntity<List<Product>> getAll() {
        System.out.println(">>> /products GET hit!");
        return ResponseEntity.ok(service.getAllProducts());
    }


    /////// Mouard added THIS ///////

//    @GetMapping("/exists")
//    public ResponseEntity<Boolean> isValidProduct(@RequestParam String productId) {
//        try {
//            Product product = service.getProductById(productId);
//            return ResponseEntity.ok(true);
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.ok(false);
//        }
//    }

    @GetMapping("/get/{id}")  //TESTED
    public ResponseEntity<?> getById(@PathVariable String id) {
        try {
            Product product = service.getProductById(id);
            return ResponseEntity.ok(product);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PutMapping("/{id}") //TESTED
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody ProductDTO dto) {
        try {
            Product updated = service.updateProduct(id, dto);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")//TESTED
    public ResponseEntity<?> delete(@PathVariable String id) {
        try {
            service.deleteProduct(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/search/name/{name}")//TESTED
    public ResponseEntity<List<Product>> searchByName(@PathVariable String name) {
        return ResponseEntity.ok(service.searchByName(name));
    }

    @GetMapping("/search/category/{category}")//TESTED
    public ResponseEntity<List<Product>> searchByCategory(@PathVariable String category) {
        return ResponseEntity.ok(service.searchByCategory(category));
    }

    @GetMapping("/search")//TESTED
    public ResponseEntity<List<Product>> searchByNameAndCategory(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category) {
        return ResponseEntity.ok(service.searchByNameAndCategory(name, category));
    }

    @GetMapping("/search/filters")// TESTED
    public ResponseEntity<List<Product>> searchWithFilters(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Boolean available) {
        return ResponseEntity.ok(service.searchWithFilters(
                name, category, minPrice, maxPrice, minRating, available));
    }

    // Standalone filter endpoints
    @GetMapping("/filter/price")//TESTED
    public ResponseEntity<List<Product>> filterByPrice(
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice) {
        return ResponseEntity.ok(service.filterByPriceRange(minPrice, maxPrice));
    }

    @GetMapping("/filter/rating") //TESTED
    public ResponseEntity<List<Product>> filterByRating(
            @RequestParam(required = false) Double minRating) {
        return ResponseEntity.ok(service.filterByRating(minRating));
    }

    @GetMapping("/filter/availability")  //TESTED
    public ResponseEntity<List<Product>> filterByAvailability(
            @RequestParam(required = false) Boolean available) {
        return ResponseEntity.ok(service.filterByAvailability(available));
    }

    @GetMapping("/filter")  //TESTED
    public ResponseEntity<List<Product>> filterByAll(
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Double minRating,
            @RequestParam(required = false) Boolean available) {
        return ResponseEntity.ok(service.filterByAll(minPrice, maxPrice, minRating, available));
    }




    @PutMapping("/{id}/price")    //TESTED
    public ResponseEntity<?> updatePrice(@PathVariable String id, @RequestParam double newPrice) {
        try {
            service.updatePrice(id, newPrice);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/{id}/price")  //TESTED
    public ResponseEntity<?> getPrice(@PathVariable String id) {
        try {
            double price = service.getPrice(id);
            return ResponseEntity.ok(price);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

}

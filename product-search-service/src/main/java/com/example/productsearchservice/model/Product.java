package com.example.productsearchservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "products")
public class Product {
    @Id
    private String id;

    private String name;
    private String category;
    private double price;
    private double rating;
    private boolean available;
    private String type;


    public Product() {

    }
    public Product(String id, String name, String category, double price, double rating, boolean available, String type) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.rating = rating;
        this.available = available;
        this.type = type;

    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    public double getRating() {
        return rating;
    }
    public void setRating(double rating) {
        this.rating = rating;
    }
    public boolean isAvailable() {
        return available;
    }
    public void setAvailable(boolean available) {
        this.available = available;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }


}

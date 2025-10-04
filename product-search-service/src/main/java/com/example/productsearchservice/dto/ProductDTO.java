package com.example.productsearchservice.dto;

public class ProductDTO {
    private String name;
    private String category;
    private double price;
    private double rating;
    private boolean available;
    private String type;

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

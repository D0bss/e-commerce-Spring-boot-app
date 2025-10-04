package com.example.adminapp.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "promotions")
public class Promotion {
    @Id
    private String id;
    private  String description;
    private DiscountType discountType;
    private  double value;
    private LocalDate validFrom;
    private LocalDate validTo;
    private boolean isActive;

    public Promotion(String description, DiscountType discountType, double value, LocalDate validFrom, LocalDate validTo, Integer usageLimit) {
        this.description = description;
        this.discountType = discountType;
        this.value = value;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.isActive = true;
    }

    public Promotion() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DiscountType getDiscountType() {
        return discountType;
    }

    public void setDiscountType(DiscountType discountType) {
        this.discountType = discountType;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public LocalDate getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDate validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDate getValidTo() {
        return validTo;
    }

    public void setValidTo(LocalDate validTo) {
        this.validTo = validTo;
    }



    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}

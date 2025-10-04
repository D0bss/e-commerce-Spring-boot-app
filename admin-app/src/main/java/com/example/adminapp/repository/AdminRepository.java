package com.example.adminapp.repository;

import com.example.adminapp.model.Admin;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface AdminRepository extends MongoRepository<Admin, String> {
    Admin findByEmail(String email);
}

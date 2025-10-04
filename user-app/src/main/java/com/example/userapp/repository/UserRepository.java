package com.example.userapp.repository;

import com.example.userapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    //List<User> id(Long id);

    //Long id(Long id);
}

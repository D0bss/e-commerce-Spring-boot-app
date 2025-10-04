package com.example.userapp.repository;

import com.example.userapp.model.User;
import com.example.userapp.model.UserProductList;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserProductListRepository extends JpaRepository<UserProductList, Long> {

    List<UserProductList> findByUser(User user);

    @Query("SELECT upl FROM UserProductList upl WHERE upl.user = :user AND :productId IN elements(upl.productIds)")
    Optional<UserProductList> findByUserAndProductId(@Param("user") User user, @Param("productId") String productId);

    @Query("SELECT CASE WHEN COUNT(upl) > 0 THEN true ELSE false END " +
            "FROM UserProductList upl WHERE upl.user = :user AND :productId IN elements(upl.productIds)")
    boolean existsByUserAndProductId(@Param("user") User user, @Param("productId") String productId);

    @Modifying
    @Transactional
    @Query("DELETE FROM UserProductList upl WHERE upl.user = :user AND :productId IN elements(upl.productIds)")
    void deleteByUserAndProductId(@Param("user") User user, @Param("productId") String productId);
}

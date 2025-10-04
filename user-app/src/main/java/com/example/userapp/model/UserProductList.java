package com.example.userapp.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProductList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> productIds;

    // Replace the old constructor with this one
    public UserProductList(User user, List<String> productIds) {
        this.user = user;
        this.productIds = productIds;
    }
}

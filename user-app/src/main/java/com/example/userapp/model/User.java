package com.example.userapp.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;
    private String name;
    private String address;
    private String preferences;
    private boolean isValidated;

    protected User() {} // JPA requirement

    private User(UserBuilder builder) {
        this.email = builder.email;
        this.password = builder.password;
        this.name = builder.name;
        this.address = builder.address;
        this.preferences = builder.preferences;
        this.isValidated = false;
    }

    public static class UserBuilder {
        private final String email;
        private final String password;
        private final String name;
        private String address;
        private String preferences;

        public UserBuilder(String email, String password, String name) {
            this.email = email;
            this.password = password;
            this.name = name;
        }

        public UserBuilder address(String address) {
            this.address = address;
            return this;
        }

        public UserBuilder preferences(String preferences) {
            this.preferences = preferences;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }

    public void validateEmail() {
        this.isValidated = true;
    }

    public boolean isValidated() {
        return isValidated;
    }
}

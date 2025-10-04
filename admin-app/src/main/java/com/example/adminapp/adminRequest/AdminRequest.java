package com.example.adminapp.adminRequest;

public class AdminRequest {

    private String email;
    private String password;

    public AdminRequest() {}
    public AdminRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

}

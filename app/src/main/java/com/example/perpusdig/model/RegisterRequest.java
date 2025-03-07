package com.example.perpusdig.model;

public class RegisterRequest {
    private String username;
    private String email_user;
    private String password_user;

    public RegisterRequest(String username, String email_user, String password_user) {
        this.username = username;
        this.email_user = email_user;
        this.password_user = password_user;
    }

    // Tambahkan getter dan setter jika diperlukan
    public String getUsername() {
        return username;
    }

    public String getEmail_user() {
        return email_user;
    }

    public String getPassword_user() {
        return password_user;
    }
}
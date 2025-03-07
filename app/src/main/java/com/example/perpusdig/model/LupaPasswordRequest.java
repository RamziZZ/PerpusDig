package com.example.perpusdig.model;

public class LupaPasswordRequest {
    private String email_user;
    private String new_password;

    // Constructor untuk inisialisasi semua field
    public LupaPasswordRequest(String email_user, String new_password) {
        this.email_user = email_user;
        this.new_password = new_password;
    }

    public LupaPasswordRequest(String newPassword) {
    }

    // Getter untuk email_user
    public String getEmail_user() {
        return email_user;
    }

    // Setter untuk email_user
    public void setEmail_user(String email_user) {
        this.email_user = email_user;
    }

    // Getter untuk new_password
    public String getNew_password() {
        return new_password;
    }

    // Setter untuk new_password
    public void setNew_password(String new_password) {
        this.new_password = new_password;
    }
}
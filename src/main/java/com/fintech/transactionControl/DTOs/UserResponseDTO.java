package com.fintech.transactionControl.DTOs;

import java.util.List;

public class UserResponseDTO {

    private String username;

    private String email;

    private String role;

    private Boolean active;

    public UserResponseDTO() {
    };

    public UserResponseDTO(String username, String email, String roles, Boolean active) {
        this.username = username;
        this.email = email;
        this.role = roles;
        this.active = active;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public Boolean getActive() {
        return active;
    }

    public String getRole() {
        return role;
    }

    public String getUsername() {
        return username;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
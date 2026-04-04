package com.fintech.transactionControl.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public class UserRequestDTO {

    @NotBlank(message = "Name is Required")
    private String username;

    @NotBlank(message = "Password is Required")
    @Size(min=6,message = "Password must be of more than 6 Characters")
    private String password;

    @Email
    @NotBlank(message = "Email is Required")
    private String email;

    private String role;

    public UserRequestDTO(String userName, String password, String email, String role){
        this.username=userName;
        this.email=email;
        this.password=password;
        this.role=role;
    }

    public void setUsername(String userName) {
        this.username = userName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public String getUsername() {
        return username;
    }

}

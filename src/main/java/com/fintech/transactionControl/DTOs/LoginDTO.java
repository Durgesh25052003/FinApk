package com.fintech.transactionControl.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginDTO {

    @NotBlank(message="Email is Required...")
    @Email(message = "Email Should be in correct Format...")
    private String email;

    @NotBlank(message = "Password is Required..")
    private String password;

    public LoginDTO(){};

    public LoginDTO(String email,String password){
        this.email=email;
        this.password=password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }
}

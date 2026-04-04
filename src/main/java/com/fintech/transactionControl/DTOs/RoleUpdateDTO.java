package com.fintech.transactionControl.DTOs;

import lombok.Data;

@Data
public class RoleUpdateDTO {
    private String role;

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}

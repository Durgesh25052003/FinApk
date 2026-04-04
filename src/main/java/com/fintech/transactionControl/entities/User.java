package com.fintech.transactionControl.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String Name;

    @Column(unique = true,nullable = false)
    private String email;

    private String password;

    private Boolean active=true;

    @ManyToOne
    @JoinColumn(name="role_id")
    private Role role;

    public User(){};

    public User(String name, String email,String password,Role role){
        this.email=email;
        this.Name=name;
        this.password=password;
        this.role=role;
    }

    public Long getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getName(){
        return Name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getRole() {
        return role.getName();
    }

    public Boolean getActive() {
        return active;
    }
}

package com.mcmovilcare.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "admin_users")
public class AdminUser {
    @Id
    @Column(length = 255)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    protected AdminUser() { }

    public AdminUser(String email, String passwordHash) {
        this.email = email;
        this.passwordHash = passwordHash;
    }

    public String email() {
        return email;
    }

    public String passwordHash() {
        return passwordHash;
    }
}
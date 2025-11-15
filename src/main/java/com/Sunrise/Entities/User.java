package com.Sunrise.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", unique = true, nullable = false, length = 30)
    private String username;

    @Column(name = "name", nullable = false, length = 30)
    private String name;

    @Column(name = "email", nullable = false, length = 60)
    private String email;

    @Column(name = "hash_password", nullable = false, length = 64)
    private String hashPassword;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    public User() {}

    public User(String username, String name, String email, String hashPassword) {
        this.username = username;
        this.name = name;
        this.email = email;
        this.hashPassword = hashPassword;
    }
}
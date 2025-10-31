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
@Table(name = "users")
public class User {

    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    @Column(length = 30, nullable = false, unique = true)
    @Size(min = 4, max = 30, message = "Username must be between 4 and 30 characters")
    @Pattern(
        regexp = "^[a-zA-Z0-9_]+$",
        message = "Username must contain only letters, digits, and underscores"
    )
    private String username;

    @Getter
    @Setter
    @Column(length = 30, nullable = false)
    @Size(min = 4, max = 30, message = "Name must be between 4 and 30 characters")
    @Pattern(
        regexp = "^[a-zA-Z0-9_]+$",
        message = "Name must contain only letters, digits, and underscores"
    )
    private String name;

    @Getter
    @Setter
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @Getter
    @Setter
    @Column(name = "hash_password", length = 64, nullable = false)
    @Size(min = 64, max = 64, message = "HashPassword must be 64 characters")
    private String hashPassword;

    private LocalDateTime last_login;
    @Column(nullable = false)
    private LocalDateTime created_at;
    @Column(nullable = false)
    private Boolean enabled = true;
    @Column(nullable = false)
    private Boolean is_deleted = false;

    public User() {}
    public User(String username, String hashPassword) {
        this.username = username;
        this.hashPassword = hashPassword;
    }
}
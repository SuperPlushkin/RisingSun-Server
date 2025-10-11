package com.RisingSun.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 30, nullable = false, unique = true)
    @Size(min = 4, max = 30, message = "Username must be between 4 and 30 characters")
    @Pattern(
        regexp = "^[a-zA-Z0-9_]+$",
        message = "Username must contain only letters, digits, and underscores"
    )
    private String username;

    @Column(length = 30, nullable = false)
    @Size(min = 4, max = 30, message = "Name must be between 4 and 30 characters")
    @Pattern(
        regexp = "^[a-zA-Z0-9_]+$",
        message = "Name must contain only letters, digits, and underscores"
    )
    private String name;

    @Column(length = 64, nullable = false)
    @Size(message = "HashPassword must be 64 characters")
    private String hash_password;

    private LocalDateTime last_login;
    @Column(nullable = false)
    private LocalDateTime created_at;
    @Column(nullable = false)
    private Boolean enabled = true;
    @Column(nullable = false)
    private Boolean is_deleted = false;

    public User() {}
    public User(String username, String hash_password) {
        this.username = username;
        this.hash_password = hash_password;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getHashPassword() { return hash_password; }
    public void setHashPassword(String hash_password) { this.hash_password = hash_password; }

    public LocalDateTime getCreationDate() { return created_at; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
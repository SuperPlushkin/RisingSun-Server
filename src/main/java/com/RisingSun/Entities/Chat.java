package com.RisingSun.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "chats")
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    @Size(min = 4, max = 50)
    @Pattern(
        regexp = "^[a-zA-Z0-9 _-]+$",
        message = "Chat name can contain letters, digits, spaces, underscores, and hyphens"
    )
    private String name;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User created_by;

    @Column(nullable = false)
    private LocalDateTime created_at = LocalDateTime.now();
    @Column(nullable = false)
    private Boolean is_deleted = false;
}

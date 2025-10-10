package com.RisingSun.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(name = "login_history")
public class LoginHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    @Size(max = 45)
    private String ip_address;
    @Column(nullable = false)
    private String device_info;
    @Column(nullable = false)
    private LocalDateTime login_at = LocalDateTime.now();
    @Column(nullable = false)
    private Boolean success = false;
}

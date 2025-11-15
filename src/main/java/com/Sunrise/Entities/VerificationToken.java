package com.Sunrise.Entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@lombok.Getter
@lombok.Setter
@Entity
@Table(name = "verification_token")
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "token_type", nullable = false)
    private String tokenType = "email_confirmation";

    // Добавляем метод для проверки истечения срока
    public boolean isExpired() {
        return expiryDate.isBefore(LocalDateTime.now());
    }
}

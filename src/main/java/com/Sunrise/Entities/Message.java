package com.Sunrise.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;

    @Column(name = "text", nullable = false)
    private String text;
    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sent_at = LocalDateTime.now();

    @Column(name = "read_count", nullable = false)
    @Min(0)
    private Long readCount = 0L;

    @Column(name = "hidden_by_admin", nullable = false)
    private Boolean hiddenByAdmin = false;
}

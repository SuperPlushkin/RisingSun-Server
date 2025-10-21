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

    @Column(nullable = false)
    private String text;
    @Column(nullable = false)
    private LocalDateTime sent_at = LocalDateTime.now();
    @Column(nullable = false)
    @Min(0)
    private Long read_count = 0L;
    @Column(nullable = false)
    private Boolean is_deleted = false;
}

package com.Sunrise.Services;

import com.Sunrise.Repositories.VerificationTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TokenCleanupService {

    private static final Logger log = LoggerFactory.getLogger(TokenCleanupService.class);
    private final VerificationTokenRepository tokenRepository;

    public TokenCleanupService(VerificationTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Scheduled(fixedRate = 24 * 60 * 60 * 1000) // каждые 24 часов
    public void cleanupExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        int deleted = tokenRepository.deleteByExpiryDateBefore(now);

        log.info("Удалено просроченных токенов: {}", deleted);
    }
}

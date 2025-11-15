package com.Sunrise.Services;

import com.Sunrise.Repositories.VerificationTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class TokenCleanupService {

    private static final Logger log = LoggerFactory.getLogger(TokenCleanupService.class);
    private final VerificationTokenRepository tokenRepository;

    public TokenCleanupService(VerificationTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Scheduled(initialDelay = 10000, fixedRate = 86400000) // Каждые 24 часа
    @Transactional
    public void cleanupExpiredTokens() {
        try
        {
            LocalDateTime now = LocalDateTime.now();
            int num = tokenRepository.deleteByExpiryDateBefore(now);

            System.out.println("✅ Expired tokens cleanup completed at " + now);
            System.out.println("Deleted tokens --> " + num);
        }
        catch (Exception e) {
            System.err.println("❌ Error during token cleanup: " + e.getMessage());
        }
    }
}

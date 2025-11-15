package com.Sunrise.Repositories;

import com.Sunrise.DTO.DBResults.TokenConfirmationResult;
import com.Sunrise.Entities.VerificationToken;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    @Query(value = "SELECT * FROM confirm_user_by_token(:token)", nativeQuery = true)
    TokenConfirmationResult confirmUserByToken(@Param("token") String token);

    @Query(value = "SELECT * FROM verification_token WHERE token = :token", nativeQuery = true)
    Optional<VerificationToken> findByToken(@Param("token") String token);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM verification_token WHERE expiry_date < :dateTime", nativeQuery = true)
    int deleteByExpiryDateBefore(@Param("dateTime") LocalDateTime time);
}

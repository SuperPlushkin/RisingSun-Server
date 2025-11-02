package com.Sunrise.Repositories;

import com.Sunrise.DTO.DB.DBTokenConfirmationResult;
import com.Sunrise.Entities.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    @Query(value = "SELECT * FROM confirm_user_by_token(:token)", nativeQuery = true)
    DBTokenConfirmationResult confirmUserByToken(@Param("token") String token);

    @Query(value = "SELECT * FROM verification_tokens v WHERE v.token = :token", nativeQuery = true)
    Optional<VerificationToken> findByToken(@Param("token") String token);
    void deleteByExpiryDateBefore(LocalDateTime time);
}

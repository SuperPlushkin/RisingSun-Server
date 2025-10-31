//package com.Sunrise.Repositories;
//
//import com.Sunrise.Entities.VerificationToken;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.time.LocalDateTime;
//import java.util.Optional;
//
//public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
//    Optional<VerificationToken> findByToken(String token);
//    void deleteByExpiryDateBefore(LocalDateTime time);
//}

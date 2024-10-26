package com.hackathon.bankingapp.Repositories;

import com.hackathon.bankingapp.Entities.OTP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface IOTPRepository extends JpaRepository<OTP, String> {
    Optional<OTP> findByIdentifierAndUsedFalse(String identifier);
    Optional<OTP> findByIdentifierAndOtpCodeAndUsedFalse(String identifier, String otpCode);
    Optional<OTP> findByIdentifierAndResetToken(String identifier, String resetToken);
    boolean existsByIdentifierAndUsedFalse(String identifier);

    int countByIdentifierAndCreatedAtAfter(String identifier, LocalDateTime date);

    @Query("SELECT MAX(o.createdAt) FROM OTP o WHERE o.identifier = :identifier AND o.attempts >= 3")
    LocalDateTime findLastFailedAttemptTime(@Param("identifier") String identifier);
}
package com.example.docmate.repository;

import com.example.docmate.entity.PasswordResetOtpEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PasswordResetOtpRepository extends JpaRepository<PasswordResetOtpEntity,String> {
    Optional<PasswordResetOtpEntity> findByUserId(String userId);
    @Query("SELECT p FROM PasswordResetOtpEntity p " +
            "WHERE p.userId = :userId " +
            "AND p.otp = :otp " +
            "AND p.isVerified = false")
    Optional<PasswordResetOtpEntity> findByUserIdAndOTP(
           String userId,
            String otp
    );
}

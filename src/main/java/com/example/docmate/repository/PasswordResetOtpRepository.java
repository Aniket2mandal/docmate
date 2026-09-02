package com.example.docmate.repository;

import com.example.docmate.entity.PasswordResetOtpEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PasswordResetOtpRepository extends JpaRepository<PasswordResetOtpEntity,String> {

    @Query("SELECT p FROM PasswordResetOtpEntity p " +
            "WHERE p.userId = :userId " +
            "AND p.isVerified = 'VERIFIED' " +
            "AND p.resetToken = :resetToken")
    Optional<PasswordResetOtpEntity> findByUserIdAndResetToken(String userId,String resetToken);

    @Query("SELECT p FROM PasswordResetOtpEntity p " +
            "WHERE p.userId = :userId " +
            "AND p.otp = :otp " +
            "AND p.isVerified = 'PENDING'")
    Optional<PasswordResetOtpEntity> findByUserIdAndOTP(
           String userId,
            String otp
    );
}

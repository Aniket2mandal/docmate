package com.example.docmate.repository;

import com.example.docmate.entity.PasswordResetOtpEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetOtpRepository extends JpaRepository<PasswordResetOtpEntity,String> {
    Optional<PasswordResetOtpEntity> findByUserId(String userId);
}

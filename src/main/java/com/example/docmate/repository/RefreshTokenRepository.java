package com.example.docmate.repository;

import com.example.docmate.entity.RefreshTokenEntity;
import com.example.docmate.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, String> {
 Optional<RefreshTokenEntity> findByToken(String token);
 void deleteByUserId(String userId);
}

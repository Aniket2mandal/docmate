package com.example.docmate.repository;

import com.example.docmate.entity.UserEntity;
import com.example.docmate.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, String> {

    Optional<UserEntity> findByEmailAndStatus(String username, UserStatus status);
    Optional<UserEntity> findByEmail(String username);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
}

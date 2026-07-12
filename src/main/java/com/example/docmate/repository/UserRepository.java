package com.example.docmate.repository;

import com.example.docmate.entity.UserEntity;
import com.example.docmate.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, String> {

    Optional<UserEntity> findByEmailAndStatus(String username, UserStatus status);
    boolean existsByEmail(String email);
}

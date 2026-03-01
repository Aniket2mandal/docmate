package com.example.docmate.repository;

import com.example.docmate.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, String> {

    Optional<UserEntity> findByEmail(String username);
    boolean existsByEmail(String email);
}

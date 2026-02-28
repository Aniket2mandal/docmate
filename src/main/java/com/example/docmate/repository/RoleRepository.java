package com.example.docmate.repository;

import com.example.docmate.entity.RoleEntity;
import com.example.docmate.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleEntity,String> {

    Optional<RoleEntity> findByName(Role roleName);
}

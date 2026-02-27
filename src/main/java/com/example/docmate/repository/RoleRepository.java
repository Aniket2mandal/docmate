package com.example.docmate.repository;

import com.example.docmate.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RoleEntity,String> {
}

package com.example.docmate.entity;

import com.example.docmate.enums.Role;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "tbl_role")
@AllArgsConstructor
@NoArgsConstructor
public class RoleEntity extends BaseEntity {
    @Column(name = "name")
    private Role name;
}

package com.example.docmate.entity;

import com.example.docmate.enums.Gender;
import com.example.docmate.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_user")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity extends BaseEntity {

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private UserStatus status=UserStatus.ACTIVE;

    @Column(name="gender")
    private Gender gender;

    @Column(name="phone")
    private String phone;

    @Column(name="address")
    private String address;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    private RoleEntity role;

    @Column(name = "role_id",insertable = false, updatable = false)
    private String roleId;

}

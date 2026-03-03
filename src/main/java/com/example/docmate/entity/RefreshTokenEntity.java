package com.example.docmate.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "tbl_refresh_token")
@Getter
@Setter
public class RefreshTokenEntity extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String token;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;

    @Column(name = "user_id",insertable = false, updatable = false)
    private String userId;

    @Column(nullable = false)
    private Instant expiryDate;  //Instant -> same as localdatetime but with timezone information and always utc format, so we can use it to compare with current time in any timezone
}

package com.example.docmate.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "tbl_doctor")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DoctorEntity extends BaseEntity {

    @Column(name="specialization")
    private String specialization;

    @Column(name="experience")
    private double experience;

    @Column(name = "qualification")
    private String qualification;

    @Column(name = "consultation_fee")
    private String consultation_fee;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;

    @Column(name = "user_id",insertable = false, updatable = false)
    private String userId;

}

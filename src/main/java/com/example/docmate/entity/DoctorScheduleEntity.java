package com.example.docmate.entity;

import com.example.docmate.enums.WeekDay;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_doctor_schedule")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DoctorScheduleEntity extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "available_day")
    private WeekDay availableDay;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "available")
    @Builder.Default
    private Boolean available = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", referencedColumnName = "id")
    private DoctorEntity doctor;

    @Column(name = "doctor_id", insertable = false, updatable = false)
    private String doctorId;
}

package com.example.docmate.entity;

import com.example.docmate.enums.ScheduleAvailabilityStatus;
import com.example.docmate.enums.WeekDay;
import jakarta.persistence.*;
import lombok.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
    private DayOfWeek availableDay;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "available")
    @Builder.Default
    private ScheduleAvailabilityStatus available = ScheduleAvailabilityStatus.AVAILABLE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", referencedColumnName = "id")
    private DoctorEntity doctor;

    @Column(name = "doctor_id", insertable = false, updatable = false)
    private String doctorId;
}

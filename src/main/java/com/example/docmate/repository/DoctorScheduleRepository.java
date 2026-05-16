package com.example.docmate.repository;

import com.example.docmate.entity.DoctorEntity;
import com.example.docmate.entity.DoctorScheduleEntity;
import com.example.docmate.enums.WeekDay;
import com.example.docmate.payload.request.DoctorScheduleRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface DoctorScheduleRepository extends JpaRepository<DoctorScheduleEntity, String> {
    List<DoctorScheduleEntity> findByDoctorIdAndStartDateAndAvailableTrue(String doctorId, LocalDate date);
   List<DoctorScheduleEntity> findByDoctorId(String doctorId);
    List<DoctorScheduleEntity> findByDoctorIdAndAvailableTrue(String doctorId);
    List<DoctorScheduleEntity> findAvailableSlotsForDoctorsByAvailableTrue(List<String> doctorIds);

    @Query("SELECT CASE WHEN COUNT(ds) > 0 THEN true ELSE false END " +
            "FROM DoctorScheduleEntity ds " +
            "WHERE ds.doctorId = :doctorId " +
            "AND ds.startDate = :scheduleDate " +
            "AND ds.startTime < :newEndTime " +
            "AND ds.endTime > :newStartTime")
    boolean existsOverlappingSchedule(
            String doctorId,
            LocalDate scheduleDate,
            LocalTime newStartTime,
            LocalTime newEndTime
    );
}

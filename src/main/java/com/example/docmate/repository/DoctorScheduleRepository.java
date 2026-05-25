package com.example.docmate.repository;

import com.example.docmate.entity.AppointmentEntity;
import com.example.docmate.entity.DoctorEntity;
import com.example.docmate.entity.DoctorScheduleEntity;
import com.example.docmate.enums.AppointmentStatus;
import com.example.docmate.enums.WeekDay;
import com.example.docmate.payload.request.DoctorScheduleRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface DoctorScheduleRepository extends JpaRepository<DoctorScheduleEntity, String> {
    List<DoctorScheduleEntity> findByDoctorIdAndStartDateAndAvailableTrue(String doctorId, LocalDate date);

    Optional<DoctorScheduleEntity> findByDoctorIdAndStartDateAndStartTimeAndAvailableTrue(
            String doctorId, LocalDate date, LocalTime startTime);

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

    @Query("SELECT ds FROM DoctorScheduleEntity ds " +
            "WHERE ds.available = :status " +
            "AND (ds.startDate < :nowDate " +
            "OR (ds.startDate = :nowDate AND ds.startTime < :nowTime)) " +
            "ORDER BY ds.startDate DESC, ds.startTime DESC")
    List<DoctorScheduleEntity> findPreviousSchedules(
            LocalDate nowDate,
            LocalTime nowTime,
            boolean status
    );
}

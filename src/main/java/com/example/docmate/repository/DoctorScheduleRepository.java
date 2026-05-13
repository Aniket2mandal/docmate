package com.example.docmate.repository;

import com.example.docmate.entity.DoctorEntity;
import com.example.docmate.entity.DoctorScheduleEntity;
import com.example.docmate.enums.WeekDay;
import com.example.docmate.payload.request.DoctorScheduleRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoctorScheduleRepository extends JpaRepository<DoctorScheduleEntity, String> {
    List<DoctorScheduleEntity> findByDoctorIdAndAvailableDayAndAvailableTrue(String doctorId, WeekDay weekDay);
   List<DoctorScheduleEntity> findByDoctorId(String doctorId);
    List<DoctorScheduleEntity> findByDoctorIdAndAvailableTrue(String doctorId);
    List<DoctorScheduleEntity> findAvailableSlotsForDoctorsByAvailableTrue(List<String> doctorIds);
}

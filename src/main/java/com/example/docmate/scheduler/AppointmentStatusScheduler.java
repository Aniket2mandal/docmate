package com.example.docmate.scheduler;
import com.example.docmate.entity.AppointmentEntity;
import com.example.docmate.entity.DoctorScheduleEntity;
import com.example.docmate.enums.AppointmentStatus;
import com.example.docmate.enums.ScheduleAvailabilityStatus;
import com.example.docmate.repository.AppointmentRepository;
import com.example.docmate.repository.DoctorScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AppointmentStatusScheduler {

    private final AppointmentRepository appointmentRepository;
    private final DoctorScheduleRepository doctorScheduleRepository;

    @Scheduled(fixedRate = 60000)
    public void updateAppointmentStatus() {

        LocalDate nowDate= LocalDate.now();
        LocalTime nowTime= LocalTime.now();

        List<AppointmentEntity> appointmentEntityList = appointmentRepository.findPreviousAppointments(
                nowDate,nowTime, AppointmentStatus.BOOKED);

        for (AppointmentEntity appointment : appointmentEntityList) {

            appointment.setStatus(AppointmentStatus.COMPLETED);
        }

        appointmentRepository.saveAll(appointmentEntityList);
    }

    @Scheduled(fixedRate = 60000)
    public void updateScheduleStatus() {

        LocalDate nowDate= LocalDate.now();
        LocalTime nowTime= LocalTime.now();

        List<DoctorScheduleEntity> scheduleEntityList = doctorScheduleRepository.findPreviousSchedules(
                nowDate,nowTime, ScheduleAvailabilityStatus.AVAILABLE);

        for (DoctorScheduleEntity scheduleEntity : scheduleEntityList) {

            scheduleEntity.setAvailable(ScheduleAvailabilityStatus.UNAVAILABLE);
        }

        doctorScheduleRepository.saveAll(scheduleEntityList);

        List<DoctorScheduleEntity> scheduleEntityBookedList = doctorScheduleRepository.findPreviousSchedules(
                nowDate,nowTime, ScheduleAvailabilityStatus.BOOKED);

        for (DoctorScheduleEntity scheduleEntity : scheduleEntityBookedList) {

            scheduleEntity.setAvailable(ScheduleAvailabilityStatus.COMPLETED);
        }

        doctorScheduleRepository.saveAll(scheduleEntityBookedList);
    }
}
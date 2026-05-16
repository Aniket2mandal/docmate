package com.example.docmate.scheduler;
import com.example.docmate.entity.AppointmentEntity;
import com.example.docmate.enums.AppointmentStatus;
import com.example.docmate.repository.AppointmentRepository;
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
}
package com.example.docmate.service.impl;

import com.example.docmate.entity.AppointmentEntity;
import com.example.docmate.entity.DoctorEntity;
import com.example.docmate.entity.DoctorScheduleEntity;
import com.example.docmate.entity.PatientEntity;
import com.example.docmate.enums.AppointmentStatus;
import com.example.docmate.enums.WeekDay;
import com.example.docmate.global.exception.GlobalException;
import com.example.docmate.global.response.GlobalResponse;
import com.example.docmate.global.response.GlobalResponseBuilder;
import com.example.docmate.payload.request.AppointmentRequest;
import com.example.docmate.repository.AppointmentRepository;
import com.example.docmate.repository.DoctorRepository;
import com.example.docmate.repository.DoctorScheduleRepository;
import com.example.docmate.repository.PatientRepository;
import com.example.docmate.service.AppointmentService;
import com.example.docmate.utils.MyConstants;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final DoctorRepository doctorRepository;
    private final DoctorScheduleRepository doctorScheduleRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final ModelMapper modelMapper;

    @Override
    public GlobalResponse bookAppointment(AppointmentRequest appointmentRequest) {

        DoctorEntity doctorEntity = doctorRepository.findById(appointmentRequest.getDoctorId())
                .orElseThrow(() -> new GlobalException("Doctor " + MyConstants.ERR_MSG_NOT_FOUND, HttpStatus.NOT_FOUND));

        PatientEntity patientEntity = patientRepository.findById(appointmentRequest.getPatientId())
                .orElseThrow(() -> new GlobalException("Patient " + MyConstants.ERR_MSG_NOT_FOUND, HttpStatus.NOT_FOUND));

        LocalDateTime appointmentTime = appointmentRequest.getAppointmentDateTime();

        WeekDay day = WeekDay.valueOf(appointmentTime.getDayOfWeek().name());

        List<DoctorScheduleEntity> schedules =
                doctorScheduleRepository.findByDoctorIdAndAvailableDayAndIsAvailableTrue(
                        doctorEntity.getId(),
                        day
                );

        boolean isAvailable = schedules.stream()
                .anyMatch(schedule ->
                        appointmentTime.isAfter(schedule.getStartTime()) &&
                                appointmentTime.isBefore(schedule.getEndTime())
                );
        if (!isAvailable) {
            throw new GlobalException(MyConstants.ERR_MSG_NOT_AVAILABLE, HttpStatus.BAD_REQUEST);
        }

        if (appointmentRepository.existsByPatientIdAndDoctorIdAndAppointmentDateTimeAndStatus(
                appointmentRequest.getPatientId(),
                appointmentRequest.getDoctorId(),
                appointmentTime,
                AppointmentStatus.BOOKED
        )){
            throw new GlobalException("You already have an appointment at this time", HttpStatus.BAD_REQUEST);
        }

        AppointmentEntity appointmentEntity = modelMapper.map(appointmentRequest, AppointmentEntity.class);
        appointmentRepository.save(appointmentEntity);

        return GlobalResponseBuilder.buildSuccessResponse("Appointment booked successfully");
    }
}

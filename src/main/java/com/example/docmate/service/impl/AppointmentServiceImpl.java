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
import com.example.docmate.payload.response.*;
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
                doctorScheduleRepository.findByDoctorIdAndAvailableDayAndAvailableTrue(
                        doctorEntity.getId(),
                        day
                );

        boolean isAvailable = schedules.stream()
                .anyMatch(schedule ->
                        appointmentTime.isAfter(schedule.getStartTime()) &&
                                appointmentTime.isBefore(schedule.getEndTime())
                );
        if (!isAvailable) {
            throw new GlobalException("Appointment " + MyConstants.ERR_MSG_NOT_AVAILABLE, HttpStatus.BAD_REQUEST);
        }

        if (appointmentRepository.existsByPatientIdAndDoctorIdAndAppointmentDateTimeAndStatus(
                appointmentRequest.getPatientId(),
                appointmentRequest.getDoctorId(),
                appointmentTime,
                AppointmentStatus.BOOKED
        )) {
            throw new GlobalException("You already have an appointment at this time", HttpStatus.BAD_REQUEST);
        }

        AppointmentEntity appointmentEntity = AppointmentEntity.builder()
                .appointmentDateTime(appointmentTime)
                .status(AppointmentStatus.BOOKED)
                .reasonForVisit(appointmentRequest.getReasonForVisit())
                .doctor(doctorEntity)
                .patient(patientEntity)
                .build();

        appointmentRepository.save(appointmentEntity);

        return GlobalResponseBuilder.buildSuccessResponse("Appointment booked successfully");
    }

    @Override
    public GlobalResponse getAllAppointment(String patientId) {
        List<AppointmentEntity> appointmentEntityList = appointmentRepository.findByPatientId(patientId);
        List<AppointmentResponse> appointmentResponseList = appointmentEntityList.stream()
                .map(appointment -> {
                            DoctorResponse doctorResponse = modelMapper.map(appointment.getDoctor(), DoctorResponse.class);
                            if (appointment.getDoctor().getUser() != null) {
                                UserResponse userResponse = modelMapper.map(appointment.getDoctor().getUser(), UserResponse.class);
                                if (appointment.getDoctor().getUser().getRole() != null) {
                                    RoleResponse roleResponse = modelMapper.map(appointment.getDoctor().getUser().getRole(), RoleResponse.class);
                                    userResponse.setRole(roleResponse.getName());
                                }
                                doctorResponse.setUser(userResponse);
                            }
//                            PatientResponse patientResponse = modelMapper.map(appointment.getPatient(), PatientResponse.class);
//                            if (appointment.getPatient().getUser() != null) {
//                                UserResponse userResponse = modelMapper.map(appointment.getPatient().getUser(), UserResponse.class);
//                                if (appointment.getDoctor().getUser().getRole() != null) {
//                                    RoleResponse roleResponse = modelMapper.map(appointment.getPatient().getUser().getRole(), RoleResponse.class);
//                                    userResponse.setRole(roleResponse.getName());
//                                }
//                                patientResponse.setUser(userResponse);
//                            }
                            AppointmentResponse appointmentResponse = AppointmentResponse.builder()
                                    .appointmentId(appointment.getId())
                                    .patientId(appointment.getPatientId())
                                    .doctor(doctorResponse)
                                    .appointmentDateTime(appointment.getAppointmentDateTime())
                                    .status(appointment.getStatus())
                                    .reasonForVisit(appointment.getReasonForVisit())
                                    .build();
                            return appointmentResponse;
                        }
                )
                .toList();
        return GlobalResponseBuilder.buildSuccessResponseWithData("All appointment fetched successfully ",appointmentResponseList);
    }

}

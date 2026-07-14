package com.example.docmate.service.impl;

import com.example.docmate.entity.AppointmentEntity;
import com.example.docmate.entity.DoctorEntity;
import com.example.docmate.entity.DoctorScheduleEntity;
import com.example.docmate.entity.PatientEntity;
import com.example.docmate.entity.UserEntity;
import com.example.docmate.enums.AppointmentStatus;
import com.example.docmate.enums.ScheduleAvailabilityStatus;
import com.example.docmate.enums.UserStatus;
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
import com.example.docmate.repository.UserRepository;
import com.example.docmate.service.AppointmentService;
import com.example.docmate.service.MailService;
import com.example.docmate.utils.CommonMethods;
import com.example.docmate.utils.MyConstants;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final MailService mailService;
    private final DoctorRepository doctorRepository;
    private final DoctorScheduleRepository doctorScheduleRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final ModelMapper modelMapper;
    private final CommonMethods commonMethods;
    private final UserRepository userRepository;

    private static final Logger log = LoggerFactory.getLogger(AppointmentServiceImpl.class);

    @Override
    public GlobalResponse bookAppointment(AppointmentRequest appointmentRequest) {

        String email = commonMethods.getAuthenticatedUserEmail();

        UserEntity userEntity = userRepository.findByEmailAndStatus(email, UserStatus.ACTIVE)
                .orElseThrow(() -> new GlobalException("User " + MyConstants.ERR_MSG_NOT_FOUND, HttpStatus.NOT_FOUND));


        LocalDate appointmentDate = appointmentRequest.getAppointmentDate();
        LocalTime appointmentTime = appointmentRequest.getAppointmentTime();

        if (appointmentDate.isBefore(LocalDate.now())) {
            throw new GlobalException("Start date must be in the future", HttpStatus.BAD_REQUEST);
        }

        if (appointmentDate.isEqual(LocalDate.now())) {
            if (appointmentTime.isBefore(LocalTime.now())) {
                throw new GlobalException("Time must be in the future", HttpStatus.BAD_REQUEST);
            }
        }

        DoctorEntity doctorEntity = doctorRepository.findById(appointmentRequest.getDoctorId())
                .orElseThrow(() -> new GlobalException("Doctor " + MyConstants.ERR_MSG_NOT_FOUND, HttpStatus.NOT_FOUND));

        PatientEntity patientEntity = patientRepository.findByUserId(userEntity.getId())
                .orElseThrow(() -> new GlobalException("Patient " + MyConstants.ERR_MSG_NOT_FOUND, HttpStatus.NOT_FOUND));

//        List<DoctorScheduleEntity> schedules =
//                doctorScheduleRepository.findByDoctorIdAndStartDateAndAvailableTrue(
//                        doctorEntity.getId(),
//                        appointmentDate
//                );
//
//        boolean isAvailable = schedules.stream()
//                .anyMatch(schedule ->
//                        !appointmentTime.isBefore(schedule.getStartTime()) &&
//                                appointmentTime.isBefore(schedule.getEndTime())
//                );
//
//        if (!isAvailable) {
//            throw new GlobalException("Appointment " + MyConstants.ERR_MSG_NOT_AVAILABLE, HttpStatus.BAD_REQUEST);
//        }
//
//        DoctorScheduleEntity doctorScheduleEntity = schedules.stream()
//                .filter(schedule ->
//                        !appointmentTime.isBefore(schedule.getStartTime()) &&
//                                appointmentTime.isBefore(schedule.getEndTime())
//                )
//                .findFirst()
//                .orElseThrow(() -> new GlobalException("Appointment " + MyConstants.ERR_MSG_NOT_AVAILABLE, HttpStatus.BAD_REQUEST));

        DoctorScheduleEntity doctorScheduleEntity =
                doctorScheduleRepository.findByDoctorIdAndStartDateAndStartTimeAndAvailable(
                                doctorEntity.getId(),
                                appointmentDate,
                                appointmentTime,
                                ScheduleAvailabilityStatus.AVAILABLE
                        )
                        .orElseThrow(() -> new GlobalException(
                                "Appointment " + MyConstants.ERR_MSG_NOT_AVAILABLE,
                                HttpStatus.BAD_REQUEST
                        ));

        if (appointmentRepository.existsByPatientIdAndDoctorIdAndAppointmentDateAndAppointmentTimeAndStatus(
                patientEntity.getId(),
                appointmentRequest.getDoctorId(),
                appointmentDate,
                appointmentTime,
                AppointmentStatus.BOOKED
        )) {
            throw new GlobalException("You already have an appointment at this time", HttpStatus.BAD_REQUEST);
        }

        if (appointmentRepository.existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatus(
                appointmentRequest.getDoctorId(),
                appointmentDate,
                appointmentTime,
                AppointmentStatus.BOOKED
        )) {
            throw new GlobalException("No appointment available at this time", HttpStatus.BAD_REQUEST);
        }

        AppointmentEntity appointmentEntity = AppointmentEntity.builder()
                .appointmentDate(appointmentDate)
                .appointmentTime(appointmentTime)
                .status(AppointmentStatus.BOOKED)
                .reasonForVisit(appointmentRequest.getReasonForVisit())
                .doctor(doctorEntity)
                .patient(patientEntity)
                .doctorSchedule(doctorScheduleEntity)
                .build();
        appointmentRepository.save(appointmentEntity);

        if (appointmentEntity.getId() != null) {
            doctorScheduleEntity.setAvailable(ScheduleAvailabilityStatus.BOOKED);
            doctorScheduleRepository.save(doctorScheduleEntity);
        }


        String subject = "Appointment Booked";
        String body = "<p>Dear " + userEntity.getFirstName() + ",</p>"
                + "<p>Your appointment has been successfully booked.</p>"
                + "<p><b>Date:</b> " + appointmentDate + "<br>"
                + "<b>Time:</b> " + appointmentTime + "</p>"
                + "<p>Regards,<br>The Docmate Team</p>";

        try {
            mailService.sendMail(email, subject, body);
        } catch (Exception e) {
            log.error("Failed to send email to {}", email, e);
        }

        return GlobalResponseBuilder.buildSuccessResponse("Appointment booked successfully");
    }

    @Override
    public GlobalResponse getAllAppointment(String patientId) {
        List<AppointmentEntity> appointmentEntityList = appointmentRepository.findByPatientId(patientId);

        List<AppointmentResponse> appointmentResponseList = mapAppointments(appointmentEntityList);

        return GlobalResponseBuilder.buildSuccessResponseWithData("All appointment fetched successfully ", appointmentResponseList);
    }

    @Override
    public GlobalResponse getPatientsUpcomingAppointment(String patientId) {

        PatientEntity patientEntity = patientRepository.findById(patientId)
                .orElseThrow(() -> new GlobalException("Patient " + MyConstants.ERR_MSG_NOT_FOUND, HttpStatus.NOT_FOUND));

        LocalDate nowDate = LocalDate.now();
        LocalTime nowTime = LocalTime.now();

        List<AppointmentEntity> appointmentEntityList =
                appointmentRepository.findUpcomingAppointmentsByPatientId(
                        patientId, nowDate, nowTime, AppointmentStatus.BOOKED);

        List<AppointmentResponse> appointmentResponseList = mapAppointments(appointmentEntityList);

        return GlobalResponseBuilder.buildSuccessResponseWithData("Upcoming appointment fetched successfully ", appointmentResponseList);
    }

    @Override
    public GlobalResponse getDoctorsUpcomingAppointment(String doctorId) {

        LocalDate nowDate = LocalDate.now();
        LocalTime nowTime = LocalTime.now();

        DoctorEntity doctorEntity = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new GlobalException("Doctor " + MyConstants.ERR_MSG_NOT_FOUND, HttpStatus.NOT_FOUND));

        List<AppointmentEntity> appointmentEntityList =
                appointmentRepository.findUpcomingAppointmentsByDoctorId(
                        doctorId, nowDate, nowTime, AppointmentStatus.BOOKED);

        List<AppointmentResponse> appointmentResponseList = mapAppointments(appointmentEntityList);

        return GlobalResponseBuilder.buildSuccessResponseWithData("Upcoming appointment fetched successfully ", appointmentResponseList);
    }

    @Override
    public GlobalResponse getPatientsPreviousAppointment(String patientId) {

        PatientEntity patientEntity = patientRepository.findById(patientId)
                .orElseThrow(() -> new GlobalException("Patient " + MyConstants.ERR_MSG_NOT_FOUND, HttpStatus.NOT_FOUND));

        LocalDate nowDate = LocalDate.now();
        LocalTime nowTime = LocalTime.now();

        List<AppointmentEntity> appointmentEntityList =
                appointmentRepository.findPreviousAppointmentsByPatientId(
                        patientId, nowDate, nowTime, AppointmentStatus.COMPLETED);

        List<AppointmentResponse> appointmentResponseList = mapAppointments(appointmentEntityList);

        return GlobalResponseBuilder.buildSuccessResponseWithData("Previous appointment fetched successfully ", appointmentResponseList);
    }


    @Override
    public GlobalResponse getDoctorsPreviousAppointment(String doctorId) {

        LocalDate nowDate = LocalDate.now();
        LocalTime nowTime = LocalTime.now();

        DoctorEntity doctorEntity = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new GlobalException("Doctor " + MyConstants.ERR_MSG_NOT_FOUND, HttpStatus.NOT_FOUND));

        List<AppointmentEntity> appointmentEntityList =
                appointmentRepository.findPreviousAppointmentsByDoctorId(
                        doctorId, nowDate, nowTime, AppointmentStatus.COMPLETED);

        List<AppointmentResponse> appointmentResponseList = mapAppointments(appointmentEntityList);

        return GlobalResponseBuilder.buildSuccessResponseWithData("Previous appointment fetched successfully ", appointmentResponseList);
    }

    @Override
    public GlobalResponse getAppointmentDetails(String appointmentId) {
        AppointmentEntity appointmentEntity = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new GlobalException("Appointment " + MyConstants.ERR_MSG_NOT_FOUND, HttpStatus.NOT_FOUND));

        AppointmentResponse appointmentResponse = modelMapper.map(appointmentEntity, AppointmentResponse.class);

        DoctorResponse doctorResponse = modelMapper.map(appointmentEntity.getDoctor(), DoctorResponse.class);
        if (appointmentEntity.getDoctor().getUser() != null) {
            UserResponse userResponse = modelMapper.map(appointmentEntity.getDoctor().getUser(), UserResponse.class);
            doctorResponse.setUser(userResponse);
        }

        PatientResponse patientResponse = modelMapper.map(appointmentEntity.getPatient(), PatientResponse.class);

        doctorResponse.setDoctorId(appointmentEntity.getDoctor().getId());
        appointmentResponse.setAppointmentDate(appointmentEntity.getAppointmentDate());
        appointmentResponse.setAppointmentTime(appointmentEntity.getAppointmentTime());
        appointmentResponse.setDoctor(doctorResponse);
        appointmentResponse.setPatient(patientResponse);
        return GlobalResponseBuilder.buildSuccessResponseWithData("Appointment details fetched successfully ", appointmentResponse);
    }


    public List<AppointmentResponse> mapAppointments(List<AppointmentEntity> appointmentEntityList) {

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

                            PatientResponse patientResponse = modelMapper.map(appointment.getPatient(), PatientResponse.class);
                            if (appointment.getPatient().getUser() != null) {
                                UserResponse userResponse = modelMapper.map(appointment.getPatient().getUser(), UserResponse.class);
                                if (appointment.getDoctor().getUser().getRole() != null) {
                                    RoleResponse roleResponse = modelMapper.map(appointment.getPatient().getUser().getRole(), RoleResponse.class);
                                    userResponse.setRole(roleResponse.getName());
                                }
                                patientResponse.setUser(userResponse);
                            }

                            AppointmentResponse appointmentResponse = AppointmentResponse.builder()
                                    .appointmentId(appointment.getId())
//                                     .patientId(appointment.getPatientId())
                                    .doctor(doctorResponse)
                                    .patient(patientResponse)
                                    .appointmentDate(appointment.getAppointmentDate())
                                    .appointmentTime(appointment.getAppointmentTime())
                                    .status(appointment.getStatus())
                                    .reasonForVisit(appointment.getReasonForVisit())
                                    .build();
                            return appointmentResponse;
                        }
                )
                .toList();

        return appointmentResponseList;
    }

}

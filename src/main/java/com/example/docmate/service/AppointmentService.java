package com.example.docmate.service;

import com.example.docmate.global.response.GlobalResponse;
import com.example.docmate.payload.request.AppointmentRequest;


public interface AppointmentService {
    GlobalResponse bookAppointment(AppointmentRequest appointmentRequest);
    GlobalResponse getAllAppointment( String patientId);
    GlobalResponse getPatientsUpcomingAppointment(String patientId);
    GlobalResponse getDoctorsUpcomingAppointment(String doctorId);
    GlobalResponse getPatientsPreviousAppointment(String patientId);
    GlobalResponse getDoctorsPreviousAppointment(String doctorId);
    GlobalResponse getAppointmentDetails(String appointmentId);



}

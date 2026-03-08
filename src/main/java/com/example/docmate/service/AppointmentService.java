package com.example.docmate.service;

import com.example.docmate.global.response.GlobalResponse;
import com.example.docmate.payload.request.AppointmentRequest;

public interface AppointmentService {
    GlobalResponse bookAppointment(AppointmentRequest appointmentRequest);
}

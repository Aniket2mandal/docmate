package com.example.docmate.controller;

import com.example.docmate.global.response.GlobalResponse;
import com.example.docmate.payload.request.AppointmentRequest;
import com.example.docmate.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/appointment")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping("/book")
    public ResponseEntity<GlobalResponse> bookAppointment(@RequestBody AppointmentRequest appointmentRequest) {
        return ResponseEntity.ok(appointmentService.bookAppointment(appointmentRequest));
    }
}

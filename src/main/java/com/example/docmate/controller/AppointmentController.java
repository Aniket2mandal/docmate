package com.example.docmate.controller;

import com.example.docmate.global.response.GlobalResponse;
import com.example.docmate.payload.request.AppointmentRequest;
import com.example.docmate.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/appointment")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping("/book")
    public ResponseEntity<GlobalResponse> bookAppointment(@RequestBody AppointmentRequest appointmentRequest) {
        return ResponseEntity.ok(appointmentService.bookAppointment(appointmentRequest));
    }
    @GetMapping("/get-all-appointment/{patientId}")
    public ResponseEntity<GlobalResponse> getAllAppointment(@PathVariable String patientId){
        return  ResponseEntity.ok(appointmentService.getAllAppointment(patientId));
    }
}

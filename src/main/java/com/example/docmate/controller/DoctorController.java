package com.example.docmate.controller;

import com.example.docmate.entity.DoctorEntity;
import com.example.docmate.enums.UserStatus;
import com.example.docmate.global.response.GlobalResponse;
import com.example.docmate.payload.request.DoctorRequest;
import com.example.docmate.payload.request.DoctorScheduleRequest;
import com.example.docmate.payload.request.UserRequest;
import com.example.docmate.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/doctor")
@RequiredArgsConstructor
public class DoctorController {
    private final DoctorService doctorService;
 @PostMapping("/create-schedule")
    public ResponseEntity<GlobalResponse> createDoctorSchedule(@RequestBody DoctorScheduleRequest scheduleRequest){
     return ResponseEntity.ok(doctorService.createDoctorSchedule(scheduleRequest));
 }
 @GetMapping("/get-all-schedule/{doctorId}")
    public ResponseEntity<GlobalResponse> getAllSchedule(@PathVariable String doctorId){
     return ResponseEntity.ok(doctorService.getAllSchedule(doctorId));
 }
 @GetMapping("/get-available-slots/{doctorId}")
    public ResponseEntity<GlobalResponse> getAvailableSlots(@PathVariable String doctorId){
     return ResponseEntity.ok(doctorService.getAvailableSlots(doctorId));
 }
}

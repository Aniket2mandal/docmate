package com.example.docmate.controller;

import com.example.docmate.entity.DoctorEntity;
import com.example.docmate.enums.UserStatus;
import com.example.docmate.global.response.GlobalResponse;
import com.example.docmate.payload.request.DoctorRequest;
import com.example.docmate.payload.request.DoctorScheduleRequest;
import com.example.docmate.payload.request.UserRequest;
import com.example.docmate.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public ResponseEntity<GlobalResponse> getAllSchedule(@PathVariable String doctorId,
                                                         @RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "9") int size){

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(doctorService.getAllSchedule(doctorId,pageable));
    }

 @GetMapping("/get-available-slots/{doctorId}")
    public ResponseEntity<GlobalResponse> getAvailableSlots(@PathVariable String doctorId,
                                                            @RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "9") int size){
     Pageable pageable = PageRequest.of(page, size);
     return ResponseEntity.ok(doctorService.getAvailableSlots(doctorId,pageable));
 }

 @DeleteMapping("/delete-schedule/{scheduleId}")
    public ResponseEntity<GlobalResponse> deleteSchedule(@PathVariable String scheduleId){
     return ResponseEntity.ok(doctorService.deleteSchedule(scheduleId));
 }

}

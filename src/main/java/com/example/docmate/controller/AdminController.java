package com.example.docmate.controller;

import com.example.docmate.entity.DoctorEntity;
import com.example.docmate.entity.RoleEntity;
import com.example.docmate.enums.Role;
import com.example.docmate.global.exception.GlobalException;
import com.example.docmate.global.response.GlobalResponse;
import com.example.docmate.global.response.GlobalResponseBuilder;
import com.example.docmate.payload.request.DoctorRequest;
import com.example.docmate.payload.request.RoleRequest;
import com.example.docmate.payload.request.UserRequest;
import com.example.docmate.service.AdminService;
import com.example.docmate.service.DoctorService;
import com.example.docmate.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;
    private final DoctorService doctorService;
    private final PatientService patientService;

    @PostMapping("/create-role")
    public ResponseEntity<GlobalResponse> createRole(@RequestBody RoleRequest role) {
        return ResponseEntity.ok(adminService.createRole(role));
    }

    @GetMapping("/get-all/role")
    public ResponseEntity<GlobalResponse> getAllRole() {
        return ResponseEntity.ok(adminService.getAllRole());
    }

    @PostMapping("/create-doctor")
    public ResponseEntity<GlobalResponse> createDoctor(@RequestBody DoctorRequest doctor) {
        return ResponseEntity.ok(doctorService.createDoctor(doctor));
    }

    @GetMapping("/get-all-doctor")
    public ResponseEntity<GlobalResponse> getAllDoctor(  @RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "9") int size) {
//        return ResponseEntity.ok(doctorService.getAllDoctor(page,size));
        try {
            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(doctorService.getAllDoctor(pageable));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(ex);
        }
    }


    @GetMapping("/get-doctor/{id}")
    public ResponseEntity<GlobalResponse> getDoctorById(@PathVariable String id) {
        return ResponseEntity.ok(doctorService.getDoctorById(id));
    }

    @PutMapping("/change-status/{userId}")
    public ResponseEntity<GlobalResponse> changeStatus(@RequestBody UserRequest user, @PathVariable String userId) {
        return ResponseEntity.ok(adminService.changeStatus(user, userId));
    }

    @PutMapping("/update-doctor/{doctorId}")
    public ResponseEntity<GlobalResponse> updateDoctor(@RequestBody DoctorRequest doctorRequest, @PathVariable String doctorId) {
        return ResponseEntity.ok(doctorService.updateDoctor(doctorRequest, doctorId));
    }

    @GetMapping("/get-all-patient")
    public ResponseEntity<GlobalResponse> getAllPatient() {
        return ResponseEntity.ok(patientService.getAllPatient());
    }
}

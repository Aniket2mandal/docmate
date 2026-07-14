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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;
    private final DoctorService doctorService;
    private final PatientService patientService;

    @PostMapping("/create-role")
    public ResponseEntity<GlobalResponse> createRole(@Valid @RequestBody RoleRequest role) {
        return ResponseEntity.ok(adminService.createRole(role));
    }

    @GetMapping("/get-all/role")
    public ResponseEntity<GlobalResponse> getAllRole() {
        return ResponseEntity.ok(adminService.getAllRole());
    }

    @PostMapping(value = "/create-doctor", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GlobalResponse> createDoctor(@Valid
                                                       @RequestPart("doctorRequest") DoctorRequest doctor,

                                                       @RequestPart("citizenshipFront") MultipartFile citizenshipFront,

                                                       @RequestPart("citizenshipBack") MultipartFile citizenshipBack,

                                                       @RequestPart("doctorLicense") MultipartFile doctorLicense,

                                                       @RequestPart("educationCertificate") MultipartFile educationCertificate
    ) {
        return ResponseEntity.ok(doctorService.createDoctor(doctor,
                citizenshipFront, citizenshipBack, doctorLicense, educationCertificate));
    }

    @GetMapping("/get-all-doctor")
    public ResponseEntity<GlobalResponse> getAllDoctor(@RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "9") int size) {
//        return ResponseEntity.ok(doctorService.getAllDoctor(page,size));
        try {
            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(doctorService.getAllDoctor(pageable));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(ex);
        }
    }


    //not needed /get-doctor/{id} already in public controller
//    @GetMapping("/get-doctor/{id}")
//    public ResponseEntity<GlobalResponse> getDoctorById(@PathVariable String id) {
//        return ResponseEntity.ok(doctorService.getDoctorById(id));
//    }

    @PutMapping("/change-status/{userId}")
    public ResponseEntity<GlobalResponse> changeStatus(@RequestBody UserRequest user, @PathVariable String userId) {
        return ResponseEntity.ok(adminService.changeStatus(user, userId));
    }

    @PutMapping(value = "/update-doctor/{doctorId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GlobalResponse> updateDoctor(@Valid
                                                       @RequestPart("doctorRequest") DoctorRequest doctorRequest,

                                                       @RequestPart(value = "citizenshipFront", required = false) MultipartFile citizenshipFront,

                                                       @RequestPart(value = "citizenshipBack", required = false) MultipartFile citizenshipBack,

                                                       @RequestPart(value = "doctorLicense", required = false) MultipartFile doctorLicense,

                                                       @RequestPart(value = "educationCertificate", required = false) MultipartFile educationCertificate,

                                                       @PathVariable String doctorId) {
        return ResponseEntity.ok(doctorService.updateDoctor(doctorRequest, doctorId,
                citizenshipFront, citizenshipBack, doctorLicense, educationCertificate));
    }

    @DeleteMapping("/delete-doctor/{doctorId}")
    public ResponseEntity<GlobalResponse> deleteDoctor(@PathVariable String doctorId) {
        return ResponseEntity.ok(doctorService.deleteDoctor(doctorId));
    }

    @GetMapping("/get-all-patient")
    public ResponseEntity<GlobalResponse> getAllPatient(@RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "9") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(patientService.getAllPatient(pageable));
    }

    @DeleteMapping("/delete-patient/{patientId}")
    public ResponseEntity<GlobalResponse> deletePatient(@PathVariable String patientId) {
        return ResponseEntity.ok(patientService.deletePatient(patientId));
    }

    @GetMapping("/get-doctor-requests")
    public ResponseEntity<GlobalResponse> getDoctorRequests(@RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "9") int size) {

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(adminService.getDoctorRequests(pageable));
    }

    @GetMapping("/get-doctor-request/{doctorRequestId}")
    public ResponseEntity<GlobalResponse> getDoctorRequests(@PathVariable String doctorRequestId) {

        return ResponseEntity.ok(adminService.getDoctorRequest(doctorRequestId));
    }

    @PostMapping("/approve-doctor-request/{doctorRequestId}")
    public ResponseEntity<GlobalResponse> approveDoctorRequest(@PathVariable String doctorRequestId){
        return ResponseEntity.ok(adminService.approveDoctorRequest(doctorRequestId));
    }

    @PutMapping("/reject-doctor-request/{doctorRequestId}")
    public ResponseEntity<GlobalResponse> rejectDoctorRequest(@PathVariable String doctorRequestId,@RequestBody String reason){
        return ResponseEntity.ok(adminService.rejectDoctorRequest(doctorRequestId,reason));
    }

//    @GetMapping("/get-all-users")
//    public ResponseEntity<GlobalResponse> getAllUsers() {
//        return ResponseEntity
//    }

}

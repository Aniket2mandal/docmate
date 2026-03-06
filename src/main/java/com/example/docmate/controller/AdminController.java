package com.example.docmate.controller;

import com.example.docmate.entity.DoctorEntity;
import com.example.docmate.entity.RoleEntity;
import com.example.docmate.enums.Role;
import com.example.docmate.global.exception.GlobalException;
import com.example.docmate.global.response.GlobalResponse;
import com.example.docmate.payload.request.DoctorRequest;
import com.example.docmate.payload.request.RoleRequest;
import com.example.docmate.payload.request.UserRequest;
import com.example.docmate.service.AdminService;
import com.example.docmate.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;
    private final DoctorService doctorService;

    @PostMapping("/create-role")
    public ResponseEntity<GlobalResponse> createRole(@RequestBody RoleRequest role) {
        return ResponseEntity.ok(adminService.createRole(role));
    }
    @GetMapping("/get-all/role")
    public ResponseEntity<GlobalResponse> getAllRole(){
            return ResponseEntity.ok(adminService.getAllRole());
    }
    @PostMapping("/create")
    public ResponseEntity<GlobalResponse> createDoctor(@RequestBody DoctorRequest doctor) {
        return ResponseEntity.ok(doctorService.createDoctor(doctor));
    }

    @GetMapping("/get-all-doctor")
    public ResponseEntity<GlobalResponse> getAllDoctor(){
        return ResponseEntity.ok(doctorService.getAllDoctor());
    }
    @GetMapping("/get-doctor/{id}")
    public ResponseEntity<GlobalResponse> getDoctorById(@PathVariable String id){
        return ResponseEntity.ok(doctorService.getDoctorById(id));
    }
    @PutMapping("/change-status/{userId}")
    public ResponseEntity<GlobalResponse> changeStatus(@RequestBody UserRequest user, @PathVariable String userId){
        return ResponseEntity.ok(doctorService.changeStatus(user, userId));
    }
    @PutMapping("/update-doctor/{doctorId}/{userId}")
    public  ResponseEntity<GlobalResponse> updateDoctor (@RequestBody DoctorRequest doctorRequest, @PathVariable String doctorId ,@PathVariable String userId){
        return ResponseEntity.ok(doctorService.updateDoctor( doctorRequest,doctorId, userId));
    }
}

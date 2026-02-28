package com.example.docmate.controller;

import com.example.docmate.entity.RoleEntity;
import com.example.docmate.enums.Role;
import com.example.docmate.global.exception.GlobalException;
import com.example.docmate.global.response.GlobalResponse;
import com.example.docmate.payload.request.RoleRequest;
import com.example.docmate.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @PostMapping("/create-role")
    public ResponseEntity<GlobalResponse> createRole(@RequestBody RoleRequest role) {
        return ResponseEntity.ok(adminService.createRole(role));
    }
    @GetMapping("/get-all/role")
    public ResponseEntity<GlobalResponse> getAllRole(){
        try {
            return ResponseEntity.ok(adminService.getAllRole());
        }
        catch(GlobalException e){
            throw new GlobalException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}

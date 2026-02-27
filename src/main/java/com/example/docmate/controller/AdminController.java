package com.example.docmate.controller;

import com.example.docmate.enums.Role;
import com.example.docmate.global.response.GlobalResponse;
import com.example.docmate.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @PostMapping("/create-role")
    public ResponseEntity<GlobalResponse> createRole(@RequestBody Role name) {

        return ResponseEntity.ok(adminService.createRole(name));
    }

}

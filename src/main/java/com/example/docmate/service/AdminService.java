package com.example.docmate.service;


import com.example.docmate.enums.Role;
import com.example.docmate.global.response.GlobalResponse;
import com.example.docmate.payload.request.RoleRequest;
import org.springframework.http.ResponseEntity;

public interface AdminService {
    GlobalResponse createRole( RoleRequest role);
    GlobalResponse getAllRole();

}

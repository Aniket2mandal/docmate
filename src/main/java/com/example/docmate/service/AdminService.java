package com.example.docmate.service;


import com.example.docmate.enums.Role;
import com.example.docmate.global.response.GlobalResponse;

public interface AdminService {
    GlobalResponse createRole( Role name);

}

package com.example.docmate.service;

import com.example.docmate.global.response.GlobalResponse;
import com.example.docmate.payload.request.UserRequest;

public interface AuthService {
    GlobalResponse registerAdmin(UserRequest user);
}

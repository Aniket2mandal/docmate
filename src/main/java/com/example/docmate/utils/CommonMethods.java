package com.example.docmate.utils;

import com.example.docmate.global.exception.GlobalException;
import com.example.docmate.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommonMethods {

    public String getAuthenticatedUserEmail() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new GlobalException(" User is not authenticated ", HttpStatus.UNAUTHORIZED);
        }
        //getName() returns the "principal" (the main identifier), which YOU set as the email when creating the JWT token! so it returns email
        String email = authentication.getName();
        return email;
    }
}

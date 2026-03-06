package com.example.docmate.payload.request;

import com.example.docmate.enums.Gender;
import com.example.docmate.enums.UserStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Gender gender;
    private String phone;
    private String address;
    private UserStatus status;
//    private String roleId;
}

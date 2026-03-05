package com.example.docmate.payload.response;

import com.example.docmate.enums.Role;
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
public class UserResponse {

    private String id;
    private String firstName;
    private String lastName;
    private String email;
    //    private String password;
    private String gender;
    private String phone;
    private String address;
    private Role role;
    private PatientResponse patientCore;
    private DoctorResponse doctorCore;
    private String imageUrl;
//    private String roleId;

}

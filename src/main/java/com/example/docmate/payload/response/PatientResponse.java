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
public class PatientResponse {

    private String patientId;
    private int age;
    private double weight;
    private double height;
    private String imageUrl;
    private UserResponse user;
//    private String firstName;
//    private String lastName;
//    private String email;
//    private String password;
//    private String gender;
//    private String phone;
//    private String address;
//    private Role role;


}

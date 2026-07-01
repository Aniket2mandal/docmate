package com.example.docmate.service;

import com.example.docmate.global.response.GlobalResponse;

public interface PatientService {
    GlobalResponse getAllPatient();
    GlobalResponse deletePatient(String patientId);
}

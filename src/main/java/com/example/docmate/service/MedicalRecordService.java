package com.example.docmate.service;

import com.example.docmate.global.response.GlobalResponse;
import com.example.docmate.payload.request.MedicalRecordRequest;
import org.springframework.web.multipart.MultipartFile;


public interface MedicalRecordService {
    GlobalResponse createMedicalRecord(MedicalRecordRequest medicalRecordRequest, MultipartFile[] testReports);
}

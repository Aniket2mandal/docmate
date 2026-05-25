package com.example.docmate.controller;

import com.example.docmate.global.response.GlobalResponse;
import com.example.docmate.payload.request.MedicalRecordRequest;
import com.example.docmate.service.MedicalRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/medical-record")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GlobalResponse> createMedicalRecord(
            @RequestPart("medicalRecordRequest") MedicalRecordRequest medicalRecordRequest,
            @RequestPart(value = "testReports", required = false) MultipartFile[] testReports
    ) {
        return ResponseEntity.ok(
                medicalRecordService.createMedicalRecord(medicalRecordRequest, testReports)
        );
    }
}

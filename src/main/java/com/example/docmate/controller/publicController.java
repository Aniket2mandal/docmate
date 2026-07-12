package com.example.docmate.controller;

import com.example.docmate.global.response.GlobalResponse;
import com.example.docmate.payload.request.DoctorRequest;
import com.example.docmate.payload.request.DoctorSearchRequest;
import com.example.docmate.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class publicController {

    private final DoctorService doctorService;

    @GetMapping("/get-all-doctor")
    public ResponseEntity<GlobalResponse> getAllDoctor(@RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "9") int size) {
//        return ResponseEntity.ok(doctorService.getAllDoctor(page,size));
        try {
            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(doctorService.getAllDoctor(pageable));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    @GetMapping("/get-doctor-details/{doctorId}")
    public ResponseEntity<GlobalResponse> getDoctorDetails(@PathVariable String doctorId) {
        return ResponseEntity.ok(doctorService.getDoctorDetails(doctorId));
    }

    //api for search doctor by province and specialization
    @PostMapping("/search-doctor")
    public ResponseEntity<GlobalResponse> searchDoctor(@RequestBody DoctorSearchRequest request,
                                                       @RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "9") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(doctorService.searchDoctor(request, pageable));
    }


    @PostMapping(value = "/apply-for-doctor", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GlobalResponse> ApplyForDoctor(@Valid
                                                       @RequestPart("doctorRequest") DoctorRequest doctorRequest,

                                                       @RequestPart("citizenshipFront") MultipartFile citizenshipFront,

                                                       @RequestPart("citizenshipBack") MultipartFile citizenshipBack,

                                                       @RequestPart("doctorLicense") MultipartFile doctorLicense,

                                                       @RequestPart("educationCertificate") MultipartFile educationCertificate
    ) {
        return ResponseEntity.ok(doctorService.ApplyForDoctor(doctorRequest,
                citizenshipFront, citizenshipBack, doctorLicense, educationCertificate));
    }
}

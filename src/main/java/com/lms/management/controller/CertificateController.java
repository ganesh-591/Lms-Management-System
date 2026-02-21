package com.lms.management.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lms.management.enums.TargetType;
import com.lms.management.model.Certificate;
import com.lms.management.repository.CertificateRepository;
import com.lms.management.service.CertificateService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/certificates")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService certificateService;
    private final CertificateRepository certificateRepository;

    // 🔐 Manual generate only
    @PostMapping("/manual-generate")
    public Certificate manualGenerate(@RequestBody Map<String, String> request) {

        Long userId = Long.parseLong(request.get("userId"));
        TargetType targetType = TargetType.valueOf(request.get("targetType"));
        Long targetId = Long.parseLong(request.get("targetId"));
        String studentName = request.get("studentName");
        String eventTitle = request.get("eventTitle");
        Double score = Double.parseDouble(request.get("score"));   // ✅ ADD THIS

        return certificateService.generateCertificateIfEligible(
                userId,
                targetType,
                targetId,
                studentName,
                eventTitle,
                score     // ✅ PASS THIS
        );
    }

    @GetMapping("/{id}")
    public Certificate getCertificateById(@PathVariable Long id) {
        return certificateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Certificate not found"));
    }

    @GetMapping("/user/{userId}")
    public List<Certificate> getCertificatesByUser(@PathVariable Long userId) {
        return certificateRepository.findByUserId(userId);
    }

    @PutMapping("/{id}/revoke")
    public Certificate revokeCertificate(
            @PathVariable Long id,
            @RequestBody Map<String, String> request
    ) {
        String reason = request.get("reason");

        certificateService.revokeCertificate(id, reason);

        return certificateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Certificate not found"));
    }

    @GetMapping("/verify")
    public Certificate verifyCertificateByToken(@RequestParam String token) {
        return certificateService.verifyCertificate(token);
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadCertificate(@PathVariable Long id) throws IOException {

        Certificate certificate = certificateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Certificate not found"));

        File file = new File(certificate.getPdfUrl());
        Resource resource = new UrlResource(file.toURI());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + file.getName() + "\"")
                .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                .body(resource);
    }
}
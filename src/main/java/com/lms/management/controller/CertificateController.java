package com.lms.management.controller;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    // =========================
    // 1️⃣ CREATE Certificate
    // =========================
    @PostMapping
    public Certificate createCertificate(@RequestBody Certificate request) {
        return certificateService.generateCertificate(
                request.getUserId(),
                request.getTargetType(),
                request.getTargetId(),
                request.getScore()
        );
    }

    // =========================
    // 2️⃣ GET Certificate by ID
    // =========================
    @GetMapping("/{id}")
    public Certificate getCertificateById(@PathVariable Long id) {
        return certificateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Certificate not found"));
    }

    // =========================
    // 3️⃣ GET All Certificates
    // =========================
    @GetMapping
    public List<Certificate> getAllCertificates() {
        return certificateRepository.findAll();
    }

    // =========================
    // 4️⃣ GET Certificates by User
    // =========================
    @GetMapping("/user/{userId}")
    public List<Certificate> getCertificatesByUser(@PathVariable Long userId) {
        return certificateRepository.findByUserId(userId);
    }

    // =========================
    // 5️⃣ REVOKE Certificate
    // =========================
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

    // =========================
    // 6️⃣ UPDATE Expiry Date
    // =========================
    @PutMapping("/{id}/expiry")
    public Certificate updateExpiry(
            @PathVariable Long id,
            @RequestBody Map<String, String> request
    ) {
        LocalDateTime expiryDate = LocalDateTime.parse(request.get("expiryDate"));

        Certificate certificate = certificateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Certificate not found"));

        certificate.setExpiryDate(expiryDate);
        certificate.setUpdatedAt(LocalDateTime.now());

        return certificateRepository.save(certificate);
    }

    // =========================
    // 7️⃣ DELETE Certificate (Hard Delete)
    // =========================
    @DeleteMapping("/{id}")
    public String deleteCertificate(@PathVariable Long id) {
        certificateRepository.deleteById(id);
        return "Certificate deleted successfully";
    }

    // =========================
    // 8️⃣ VERIFY Certificate (Public)
    // =========================
    @PostMapping("/verify")
    public Certificate verifyCertificate(@RequestBody Map<String, String> request) {
        return certificateService.verifyCertificate(request.get("token"));
    }

    // =========================
    // 9️⃣ DOWNLOAD Certificate PDF
    // =========================
    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> downloadCertificate(@PathVariable Long id) throws IOException {

        Certificate certificate = certificateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Certificate not found"));

        if (certificate.getPdfUrl() == null) {
            throw new RuntimeException("PDF not generated yet");
        }

        File file = new File(certificate.getPdfUrl());
        Resource resource = new UrlResource(file.toURI());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + file.getName() + "\"")
                .body(resource);
    }
    
    @GetMapping("/verify")
    public Certificate verifyCertificateByToken(@RequestParam String token) {
        return certificateService.verifyCertificate(token);
    }
}

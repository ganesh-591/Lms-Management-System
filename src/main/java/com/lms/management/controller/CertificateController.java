package com.lms.management.controller;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.ClassPathResource;
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

    // =========================================================
    // 🔐 MANUAL GENERATE
    // =========================================================
    @PostMapping("/manual-generate")
    public Certificate manualGenerate(@RequestBody Map<String, String> request) {

        Long userId = Long.parseLong(request.get("userId"));
        TargetType targetType = TargetType.valueOf(request.get("targetType"));
        Long targetId = Long.parseLong(request.get("targetId"));
        String studentName = request.get("studentName");
        String eventTitle = request.get("eventTitle");

        // ❌ Score removed (now auto fetched from DB)

        return certificateService.generateCertificateIfEligible(
                userId,
                targetType,
                targetId,
                studentName,
                eventTitle,
                null   // score not needed anymore
        );
    }

    // =========================================================
    // 🔎 VERIFY CERTIFICATE (GET - Query Param)
    // =========================================================
    @GetMapping("/verify")
    public Certificate verifyCertificate(@RequestParam String token) {
        return certificateService.verifyCertificate(token);
    }

    @PostMapping("/verify")
    public Certificate verifyCertificatePost(
            @RequestBody Map<String, String> request
    ) {
        String token = request.get("token");
        return certificateService.verifyCertificate(token);
    }

    // =========================================================
    // 🌍 PUBLIC VERIFY (QR LINK)
    // =========================================================
    @GetMapping("/public/{token}")
    public ResponseEntity<String> verifyCertificatePublic(@PathVariable String token)
            throws IOException {

        Certificate certificate = certificateService.verifyCertificate(token);

        ClassPathResource resource =
                new ClassPathResource("templates/certificate-verification.html");

        String html = new String(resource.getInputStream().readAllBytes(),
                StandardCharsets.UTF_8);

        String statusMessage;

        switch (certificate.getStatus()) {
            case ACTIVE:
                statusMessage = "VALID";
                break;
            case REVOKED:
                statusMessage = "REVOKED";
                break;
            case EXPIRED:
                statusMessage = "EXPIRED";
                break;
            default:
                statusMessage = "UNKNOWN";
        }
        
        html = html.replace("{{studentName}}", certificate.getStudentName())
                .replace("{{eventTitle}}", certificate.getEventTitle())
                .replace("{{score}}", String.valueOf(certificate.getScore()))
                .replace("{{issuedDate}}",
                         certificate.getIssuedDate().toLocalDate().toString())
                .replace("{{status}}", statusMessage)
                .replace("{{certificateId}}", certificate.getCertificateId());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, "text/html")
                .body(html);
    }

    // =========================================================
    // 👤 GET CERTIFICATES BY USER
    // =========================================================
    @GetMapping("/user/{userId}")
    public List<Certificate> getCertificatesByUser(@PathVariable Long userId) {
        return certificateRepository.findByUserId(userId);
    }

    // =========================================================
    // 🚫 REVOKE CERTIFICATE
    // =========================================================
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

    // =========================================================
    // ⏳ SET / UPDATE EXPIRY DATE
    // =========================================================
    @PutMapping("/{id}/expiry")
    public Certificate updateExpiry(
            @PathVariable Long id,
            @RequestBody Map<String, String> request
    ) {

        String expiryDateStr = request.get("expiryDate");

        certificateService.updateExpiryDate(id, expiryDateStr);

        return certificateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Certificate not found"));
    }

    // =========================================================
    // 📥 VIEW PDF INLINE
    // =========================================================
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

    // =========================================================
    // 📄 GET CERTIFICATE BY ID
    // IMPORTANT: Keep this LAST to avoid route conflicts
    // =========================================================
    @GetMapping("/{id}")
    public Certificate getCertificateById(@PathVariable Long id) {
        return certificateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Certificate not found"));
    }
}
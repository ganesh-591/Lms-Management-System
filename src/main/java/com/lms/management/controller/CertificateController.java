package com.lms.management.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lms.management.model.Certificate;
import com.lms.management.service.CertificateService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/certificates")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService certificateService;

    /**
     * Generate Certificate
     * Accepts JSON body
     */
    @PostMapping("/generate")
    public Certificate generateCertificate(
            @RequestBody Certificate request
    ) {
        return certificateService.generateCertificate(
                request.getUserId(),
                request.getTargetType(),
                request.getTargetId(),
                request.getScore()
        );
    }

    /**
     * Public Verification
     * Example:
     * GET /api/certificates/verify?token=xxxxx
     */
    @GetMapping("/verify")
    public Certificate verifyCertificate(
            @RequestParam String token
    ) {
        return certificateService.verifyCertificate(token);
    }

    /**
     * Revoke Certificate (Admin)
     * Example:
     * PUT /api/certificates/1/revoke?reason=Violation
     */
    @PutMapping("/{id}/revoke")
    public String revokeCertificate(
            @PathVariable Long id,
            @RequestParam String reason
    ) {
        certificateService.revokeCertificate(id, reason);
        return "Certificate revoked successfully";
    }
}

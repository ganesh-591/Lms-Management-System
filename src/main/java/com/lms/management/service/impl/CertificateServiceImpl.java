package com.lms.management.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.lms.management.enums.CertificateStatus;
import com.lms.management.enums.TargetType;
import com.lms.management.model.Certificate;
import com.lms.management.model.CertificateAuditLog;
import com.lms.management.repository.CertificateAuditLogRepository;
import com.lms.management.repository.CertificateRepository;
import com.lms.management.repository.ExamAttemptRepository;
import com.lms.management.service.CertificateEligibilityService;
import com.lms.management.service.CertificatePdfService;
import com.lms.management.service.CertificateService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CertificateServiceImpl implements CertificateService {

    private final CertificateRepository certificateRepository;
    private final CertificatePdfService certificatePdfService;
    private final CertificateAuditLogRepository auditLogRepository;
    private final CertificateEligibilityService certificateEligibilityService;
    private final ExamAttemptRepository examAttemptRepository;
    

    @Override
    public Certificate generateCertificateIfEligible(
            Long userId,
            TargetType targetType,
            Long targetId,
            String studentName,
            String eventTitle,
            Double score   // TEMP: coming from request
    ) {

        /*
         =========================================================
         🔐 ELIGIBILITY BLOCK (TEMP DISABLED)
         =========================================================

        boolean eligible = certificateEligibilityService
                .isEligible(userId, targetType.name(), targetId);

        if (!eligible) {
            throw new IllegalStateException("User is not eligible for certificate");
        }
        */

        /*
         =========================================================
         📊 AUTO SCORE FETCH BLOCK (TEMP DISABLED)
         =========================================================

        ExamAttempt attempt = examAttemptRepository
                .findTopByStudentIdAndExamIdAndStatusOrderByScoreDesc(
                        userId,
                        targetId,
                        "EVALUATED"
                )
                .orElseThrow(() -> new IllegalStateException(
                        "No evaluated exam attempt found"
                ));

        if (attempt.getScore() == null) {
            throw new IllegalStateException(
                    "Exam attempt score is not available"
            );
        }

        score = attempt.getScore();
        */

        // ❌ Prevent duplicate certificate
        if (certificateRepository.existsByUserIdAndTargetTypeAndTargetId(
                userId, targetType, targetId)) {

            throw new IllegalStateException("Certificate already exists");
        }

        // 🆔 Generate certificate ID
        String certificateId = "CERT-" + UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();

        // 🔑 Generate verification token
        String verificationToken = UUID.randomUUID().toString();

        // 🏗 Create certificate
        Certificate certificate = Certificate.builder()
                .certificateId(certificateId)
                .verificationToken(verificationToken)
                .userId(userId)          // still stored in user_id column
                .studentName(studentName)
                .targetType(targetType)
                .targetId(targetId)
                .eventTitle(eventTitle)
                .score(score)            // TEMP: using request score
                .issuedDate(LocalDateTime.now())
                .status(CertificateStatus.ACTIVE)
                .version(1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Certificate saved = certificateRepository.save(certificate);

        // 📝 Audit Log
        auditLogRepository.save(
                CertificateAuditLog.builder()
                        .certificateId(saved.getId())
                        .action("GENERATED")
                        .performedBy(userId)
                        .actionDate(LocalDateTime.now())
                        .remarks("Manual certificate generation (TEMP mode)")
                        .build()
        );

        // 📄 Generate PDF
        String pdfPath = certificatePdfService.generatePdf(
                saved,
                studentName,
                eventTitle
        );

        saved.setPdfUrl(pdfPath);
        saved.setUpdatedAt(LocalDateTime.now());

        return certificateRepository.save(saved);
    }

    @Override
    public Certificate verifyCertificate(String token) {

        return certificateRepository.findByVerificationToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid certificate token"));
    }

    @Override
    public void revokeCertificate(Long certificateId, String reason) {

        Certificate certificate = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new IllegalArgumentException("Certificate not found"));

        certificate.setStatus(CertificateStatus.REVOKED);
        certificate.setRevokedReason(reason);
        certificate.setRevokedAt(LocalDateTime.now());
        certificate.setUpdatedAt(LocalDateTime.now());

        certificateRepository.save(certificate);

        // 📝 Audit Log - REVOKED
        auditLogRepository.save(
                CertificateAuditLog.builder()
                        .certificateId(certificateId)
                        .action("REVOKED")
                        .performedBy(null)
                        .actionDate(LocalDateTime.now())
                        .remarks(reason)
                        .build()
        );
    }
}
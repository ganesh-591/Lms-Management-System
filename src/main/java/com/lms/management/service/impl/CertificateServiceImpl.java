package com.lms.management.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.lms.management.enums.CertificateStatus;
import com.lms.management.enums.TargetType;
import com.lms.management.model.Certificate;
import com.lms.management.model.CertificateAuditLog;
import com.lms.management.model.CertificateProgress;
import com.lms.management.repository.CertificateAuditLogRepository;
import com.lms.management.repository.CertificateProgressRepository;
import com.lms.management.repository.CertificateRepository;
import com.lms.management.service.CertificatePdfService;
import com.lms.management.service.CertificateService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CertificateServiceImpl implements CertificateService {

    private final CertificateRepository certificateRepository;
    private final CertificatePdfService certificatePdfService;
    private final CertificateAuditLogRepository auditLogRepository;
    private final CertificateProgressRepository certificateProgressRepository;

    @Override
    public Certificate generateCertificateIfEligible(
            Long userId,
            TargetType targetType,
            Long targetId,
            String studentName,
            String eventTitle,
            Double score // will NOT be used anymore
    ) {

    	// =========================================================
    	// 🔐 1️⃣ READ PROGRESS & CHECK ELIGIBILITY
    	// =========================================================
    	// 🔐 1️⃣ READ OR CREATE PROGRESS (HYBRID MODE)
    	// =========================================================
    	CertificateProgress progress =
    	        certificateProgressRepository
    	                .findByUserIdAndTargetTypeAndTargetId(
    	                        userId,
    	                        targetType,
    	                        targetId
    	                )
    	                .orElse(null);

    	// 🔹 If no progress → Manual override mode
    	if (progress == null) {

    	    progress = new CertificateProgress();
    	    progress.setUserId(userId);
    	    progress.setTargetType(targetType);
    	    progress.setTargetId(targetId);
    	    progress.setScore(score); // from request
    	    progress.setEligibilityStatus(
    	            com.lms.management.enums.CertificateEligibilityStatus.ELIGIBLE
    	    );
    	    progress.setUpdatedAt(LocalDateTime.now());

    	    certificateProgressRepository.save(progress);

    	} else {

    	    // 🔹 If progress exists → enforce eligibility rule
    	    if (progress.getEligibilityStatus() == null ||
    	        !progress.getEligibilityStatus().name().equals("ELIGIBLE")) {

    	        throw new IllegalStateException("User is not eligible for certificate");
    	    }
    	}

    	Double finalScore = progress.getScore() != null ? progress.getScore() : 0.0;

        // =========================================================
        // ❌ 3️⃣ PREVENT DUPLICATE CERTIFICATE
        // =========================================================
        if (certificateRepository.existsByUserIdAndTargetTypeAndTargetId(
                userId, targetType, targetId)) {

            throw new IllegalStateException("Certificate already exists");
        }

        // =========================================================
        // 🆔 4️⃣ GENERATE IDS
        // =========================================================
        String certificateId = "CERT-" + UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();

        String verificationToken = UUID.randomUUID().toString();

        // =========================================================
        // 🏗 5️⃣ CREATE CERTIFICATE
        // =========================================================
        Certificate certificate = Certificate.builder()
                .certificateId(certificateId)
                .verificationToken(verificationToken)
                .userId(userId)
                .studentName(studentName)
                .targetType(targetType)
                .targetId(targetId)
                .eventTitle(eventTitle)
                .score(finalScore) // 🔥 using DB score
                .issuedDate(LocalDateTime.now())
                .status(CertificateStatus.ACTIVE)
                .version(1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Certificate saved = certificateRepository.save(certificate);

        // =========================================================
        // 📝 6️⃣ AUDIT LOG
        // =========================================================
        auditLogRepository.save(
                CertificateAuditLog.builder()
                        .certificateId(saved.getId())
                        .action("GENERATED")
                        .performedBy(userId)
                        .actionDate(LocalDateTime.now())
                        .remarks("Manual certificate generation")
                        .build()
        );

        // =========================================================
        // 📄 7️⃣ GENERATE PDF
        // =========================================================
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

    @Override
    public void updateExpiryDate(Long certificateId, String expiryDateStr) {

        Certificate certificate = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new RuntimeException("Certificate not found"));

        LocalDateTime expiryDate = LocalDateTime.parse(expiryDateStr);

        certificate.setExpiryDate(expiryDate);
        certificate.setUpdatedAt(LocalDateTime.now());

        if (expiryDate.isBefore(LocalDateTime.now())) {
            certificate.setStatus(CertificateStatus.EXPIRED);
        } else {
            certificate.setStatus(CertificateStatus.ACTIVE);
        }

        certificateRepository.save(certificate);
    }
}
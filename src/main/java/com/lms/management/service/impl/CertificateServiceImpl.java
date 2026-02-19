package com.lms.management.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.lms.management.enums.CertificateStatus;
import com.lms.management.enums.TargetType;
import com.lms.management.model.Certificate;
import com.lms.management.repository.CertificateRepository;
import com.lms.management.service.CertificateService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CertificateServiceImpl implements CertificateService {

    private final CertificateRepository certificateRepository;

    @Override
    public Certificate generateCertificate(
            Long userId,
            TargetType targetType,
            Long targetId,
            Double score
    ) {

        // 1️⃣ Prevent duplicate
        if (certificateRepository.existsByUserIdAndTargetTypeAndTargetId(
                userId, targetType, targetId)) {

            throw new IllegalStateException("Certificate already exists");
        }

        // 2️⃣ Generate certificate ID
        String certificateId = "CERT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // 3️⃣ Generate secure token
        String verificationToken = UUID.randomUUID().toString();

        // 4️⃣ Create certificate object
        Certificate certificate = Certificate.builder()
                .certificateId(certificateId)
                .verificationToken(verificationToken)
                .userId(userId)
                .targetType(targetType)
                .targetId(targetId)
                .score(score)
                .issuedDate(LocalDateTime.now())
                .status(CertificateStatus.ACTIVE)
                .version(1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return certificateRepository.save(certificate);
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
    }
}

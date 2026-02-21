package com.lms.management.service;

import com.lms.management.enums.TargetType;
import com.lms.management.model.Certificate;

public interface CertificateService {

    // 🔐 Manual generation with rule validation
    Certificate generateCertificateIfEligible(
            Long userId,
            TargetType targetType,
            Long targetId,
            String studentName,
            String eventTitle,
            Double score
    );

    Certificate verifyCertificate(String token);

    void revokeCertificate(Long certificateId, String reason);
}
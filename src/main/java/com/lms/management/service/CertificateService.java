package com.lms.management.service;

import com.lms.management.model.Certificate;
import com.lms.management.enums.TargetType;

public interface CertificateService {

    Certificate generateCertificate(
            Long userId,
            TargetType targetType,
            Long targetId,
            Double score
    );

    Certificate verifyCertificate(String token);

    void revokeCertificate(Long certificateId, String reason);
}

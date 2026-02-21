package com.lms.management.service;

import com.lms.management.enums.TargetType;

public interface CertificateEligibilityService {

    boolean isEligible(
            Long userId,
            TargetType targetType,
            Long targetId
    );
}
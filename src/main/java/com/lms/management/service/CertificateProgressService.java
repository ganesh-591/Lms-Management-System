package com.lms.management.service;

import com.lms.management.enums.TargetType;

public interface CertificateProgressService {

    void updateExamProgress(Long userId,
                            TargetType targetType,
                            Long targetId,
                            Double score);

    void manualUpdateProgress(Long userId,
                              TargetType targetType,
                              Long targetId,
                              Double score);
}
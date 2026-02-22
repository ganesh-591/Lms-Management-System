package com.lms.management.service.impl;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.lms.management.enums.CertificateEligibilityStatus;
import com.lms.management.enums.TargetType;
import com.lms.management.model.CertificateProgress;
import com.lms.management.model.CertificateRule;
import com.lms.management.model.ExamAttempt;
import com.lms.management.repository.CertificateProgressRepository;
import com.lms.management.repository.CertificateRuleRepository;
import com.lms.management.repository.ExamAttemptRepository;
import com.lms.management.service.CertificateEligibilityService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CertificateEligibilityServiceImpl implements CertificateEligibilityService {

    private final CertificateRuleRepository certificateRuleRepository;
    private final ExamAttemptRepository examAttemptRepository;
    private final CertificateProgressRepository certificateProgressRepository;

    @Override
    public boolean isEligible(Long userId,
                              TargetType targetType,
                              Long targetId) {

        // 🔵 STEP 0 – First check certificate_progress
        Optional<CertificateProgress> progressOpt =
                certificateProgressRepository
                        .findByUserIdAndTargetTypeAndTargetId(
                                userId, targetType, targetId
                        );

        if (progressOpt.isPresent()) {
            return progressOpt.get().getEligibilityStatus() ==
                    CertificateEligibilityStatus.ELIGIBLE;
        }


        // 🔐 Currently support only EXAM
        if (targetType != TargetType.EXAM) {
            return false;
        }

        // 1️⃣ Fetch active rule
        Optional<CertificateRule> ruleOpt =
                certificateRuleRepository
                        .findByTargetTypeAndTargetIdAndIsActiveTrue(
                                targetType.name(),   // stored as VARCHAR
                                targetId
                        );

        if (ruleOpt.isEmpty()) {
            return false; // No rule defined
        }

        CertificateRule rule = ruleOpt.get();

        // 2️⃣ Fetch highest evaluated attempt
        Optional<ExamAttempt> attemptOpt =
                examAttemptRepository
                        .findTopByStudentIdAndExamIdAndStatusOrderByScoreDesc(
                                userId,      // studentId
                                targetId,    // examId
                                "EVALUATED"
                        );

        if (attemptOpt.isEmpty()) {
            return false; // No evaluated attempt found
        }

        ExamAttempt attempt = attemptOpt.get();

        if (attempt.getScore() == null) {
            return false;
        }

        // 3️⃣ Compare score
        BigDecimal attemptScore = BigDecimal.valueOf(attempt.getScore());

        return attemptScore.compareTo(rule.getRequiredScore()) >= 0;
    }
    
    
}
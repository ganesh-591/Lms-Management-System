package com.lms.management.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.lms.management.enums.CertificateEligibilityStatus;
import com.lms.management.enums.TargetType;
import com.lms.management.model.CertificateProgress;
import com.lms.management.model.CertificateRule;
import com.lms.management.repository.CertificateProgressRepository;
import com.lms.management.repository.CertificateRuleRepository;
import com.lms.management.service.CertificateProgressService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CertificateProgressServiceImpl implements CertificateProgressService {

    private final CertificateProgressRepository progressRepository;
    private final CertificateRuleRepository ruleRepository;

    @Override
    public void updateExamProgress(Long userId,
                                   TargetType targetType,
                                   Long targetId,
                                   Double score) {

        processProgress(userId, targetType, targetId, score);
    }

    @Override
    public void manualUpdateProgress(Long userId,
                                     TargetType targetType,
                                     Long targetId,
                                     Double score) {

        processProgress(userId, targetType, targetId, score);
    }

    private void processProgress(Long userId,
                                 TargetType targetType,
                                 Long targetId,
                                 Double score) {

        // 1️⃣ Fetch rule
        Optional<CertificateRule> ruleOpt =
                ruleRepository.findByTargetTypeAndTargetIdAndIsActiveTrue(
                        targetType.name(),
                        targetId
                );

        if (ruleOpt.isEmpty()) {
            return; // No rule defined → no progress update
        }

        CertificateRule rule = ruleOpt.get();

        // 2️⃣ Determine eligibility
        CertificateEligibilityStatus status =
                CertificateEligibilityStatus.NOT_ELIGIBLE;

        if (score != null && rule.getRequiredScore() != null) {
            BigDecimal studentScore = BigDecimal.valueOf(score);

            if (studentScore.compareTo(rule.getRequiredScore()) >= 0) {
                status = CertificateEligibilityStatus.ELIGIBLE;
            }
        }

        // 3️⃣ Fetch existing progress or create new
        CertificateProgress progress =
                progressRepository
                        .findByUserIdAndTargetTypeAndTargetId(
                                userId, targetType, targetId
                        )
                        .orElse(
                                CertificateProgress.builder()
                                        .userId(userId)
                                        .targetType(targetType)
                                        .targetId(targetId)
                                        .build()
                        );

        // 4️⃣ Update fields
        progress.setScore(score);
        progress.setEligibilityStatus(status);
        progress.setUpdatedAt(LocalDateTime.now());

        progressRepository.save(progress);
    }
}
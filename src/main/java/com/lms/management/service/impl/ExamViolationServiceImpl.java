package com.lms.management.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.management.model.ExamAttempt;
import com.lms.management.model.ExamViolation;
import com.lms.management.repository.ExamAttemptRepository;
import com.lms.management.repository.ExamViolationRepository;
import com.lms.management.service.ExamViolationService;

@Service
@Transactional
public class ExamViolationServiceImpl
        implements ExamViolationService {

    private final ExamViolationRepository examViolationRepository;
    private final ExamAttemptRepository examAttemptRepository;

    public ExamViolationServiceImpl(
            ExamViolationRepository examViolationRepository,
            ExamAttemptRepository examAttemptRepository) {
        this.examViolationRepository = examViolationRepository;
        this.examAttemptRepository = examAttemptRepository;
    }

    // ================= RECORD VIOLATION =================

    @Override
    public ExamViolation recordViolation(
            Long attemptId, String violationType) {

        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() ->
                        new IllegalStateException("Attempt not found"));

        if (!"IN_PROGRESS".equals(attempt.getStatus())) {
            throw new IllegalStateException(
                    "Cannot record violation for this attempt");
        }

        ExamViolation violation = new ExamViolation();
        violation.setAttemptId(attemptId);
        violation.setViolationType(violationType);
        violation.setViolationTime(LocalDateTime.now());

        return examViolationRepository.save(violation);
    }

    // ================= COUNT VIOLATIONS =================

    @Override
    public long getViolationCount(Long attemptId) {
        return examViolationRepository.countByAttemptId(attemptId);
    }
}

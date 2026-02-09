package com.lms.management.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.management.model.ExamAttempt;
import com.lms.management.model.ExamProctoring;
import com.lms.management.model.ExamViolation;
import com.lms.management.repository.ExamAttemptRepository;
import com.lms.management.repository.ExamProctoringRepository;
import com.lms.management.repository.ExamViolationRepository;
import com.lms.management.service.ExamAttemptService;
import com.lms.management.service.ExamViolationService;

@Service
@Transactional
public class ExamViolationServiceImpl implements ExamViolationService {

    private final ExamViolationRepository examViolationRepository;
    private final ExamAttemptRepository examAttemptRepository;
    private final ExamProctoringRepository examProctoringRepository;
    private final ExamAttemptService examAttemptService;

    public ExamViolationServiceImpl(
            ExamViolationRepository examViolationRepository,
            ExamAttemptRepository examAttemptRepository,
            ExamProctoringRepository examProctoringRepository,
            ExamAttemptService examAttemptService) {

        this.examViolationRepository = examViolationRepository;
        this.examAttemptRepository = examAttemptRepository;
        this.examProctoringRepository = examProctoringRepository;
        this.examAttemptService = examAttemptService;
    }

    // ================= RECORD VIOLATION =================
    @Override
    public ExamViolation recordViolation(
            Long attemptId, String violationType) {

        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new IllegalStateException("Attempt not found"));

        // If already auto-submitted / submitted, ignore silently
        if (!"IN_PROGRESS".equals(attempt.getStatus())) {
            return null;
        }

        ExamViolation violation = new ExamViolation();
        violation.setAttemptId(attemptId);
        violation.setViolationType(violationType);
        violation.setViolationTime(LocalDateTime.now());

        examViolationRepository.save(violation);

        // ================= AUTO-SUBMIT CHECK =================
        ExamProctoring proctoring = examProctoringRepository
                .findByExamId(attempt.getExamId())
                .orElse(null);

        if (proctoring != null && Boolean.TRUE.equals(proctoring.getEnabled())) {

            long count = examViolationRepository.countByAttemptId(attemptId);

            // Trigger auto-submit as soon as limit is reached
            if (count >= proctoring.getViolationLimit()) {
                examAttemptService.autoSubmitAttempt(
                        attemptId,
                        attempt.getStudentId()
                );
            }
        }

        return violation;
    }

    // ================= COUNT VIOLATIONS =================
    @Override
    public long getViolationCount(Long attemptId) {
        return examViolationRepository.countByAttemptId(attemptId);
    }

    @Override
    public List<ExamViolation> getViolationsByAttempt(Long attemptId) {
        return examViolationRepository.findByAttemptId(attemptId);
    }
}

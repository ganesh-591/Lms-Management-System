package com.lms.management.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.management.model.ExamAttempt;
import com.lms.management.model.ExamEvaluationLog;
import com.lms.management.repository.ExamAttemptRepository;
import com.lms.management.repository.ExamEvaluationLogRepository;
import com.lms.management.service.ExamEvaluationLogService;

@Service
@Transactional
public class ExamEvaluationLogServiceImpl
        implements ExamEvaluationLogService {

    private final ExamEvaluationLogRepository evaluationLogRepository;
    private final ExamAttemptRepository examAttemptRepository;

    public ExamEvaluationLogServiceImpl(
            ExamEvaluationLogRepository evaluationLogRepository,
            ExamAttemptRepository examAttemptRepository) {
        this.evaluationLogRepository = evaluationLogRepository;
        this.examAttemptRepository = examAttemptRepository;
    }

    // ================= LOG SCORE CHANGE =================

    @Override
    public ExamEvaluationLog logEvaluationChange(
            Long attemptId,
            Long evaluatorId,
            Double oldScore,
            Double newScore,
            String reason) {

        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() ->
                        new IllegalStateException("Attempt not found"));

        // Only after submission
        if (!"SUBMITTED".equals(attempt.getStatus())
                && !"AUTO_SUBMITTED".equals(attempt.getStatus())
                && !"EVALUATED".equals(attempt.getStatus())) {
            throw new IllegalStateException(
                    "Evaluation not allowed in current state");
        }

        ExamEvaluationLog log = new ExamEvaluationLog();
        log.setAttemptId(attemptId);
        log.setEvaluatorId(evaluatorId);
        log.setOldScore(oldScore);
        log.setNewScore(newScore);
        log.setReason(reason);
        log.setUpdatedAt(LocalDateTime.now());

        return evaluationLogRepository.save(log);
    }

    // ================= FETCH AUDIT LOGS =================

    @Override
    public List<ExamEvaluationLog> getLogsByAttempt(Long attemptId) {
        return evaluationLogRepository.findByAttemptId(attemptId);
    }
}

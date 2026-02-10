package com.lms.management.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.management.model.ExamAttempt;
import com.lms.management.model.ExamEvaluationLog;
import com.lms.management.model.ExamResponse;
import com.lms.management.repository.ExamAttemptRepository;
import com.lms.management.repository.ExamEvaluationLogRepository;
import com.lms.management.repository.ExamResponseRepository;
import com.lms.management.service.ExamEvaluationLogService;

@Service
@Transactional
public class ExamEvaluationLogServiceImpl
        implements ExamEvaluationLogService {

    private final ExamEvaluationLogRepository evaluationLogRepository;
    private final ExamAttemptRepository examAttemptRepository;
    private final ExamResponseRepository examResponseRepository;

    public ExamEvaluationLogServiceImpl(
            ExamEvaluationLogRepository evaluationLogRepository,
            ExamAttemptRepository examAttemptRepository,
            ExamResponseRepository examResponseRepository) {

        this.evaluationLogRepository = evaluationLogRepository;
        this.examAttemptRepository = examAttemptRepository;
        this.examResponseRepository = examResponseRepository;
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

    // ================= CALCULATE CURRENT SCORE =================
    @Override
    public Double getCurrentScore(Long attemptId) {

        return examResponseRepository.findByAttemptId(attemptId)
                .stream()
                .mapToDouble(r ->
                        r.getMarksAwarded() == null ? 0.0 : r.getMarksAwarded()
                )
                .sum();
    }

    // ================= EVALUATE CODING RESPONSE =================
    @Override
    public void evaluateCodingResponse(
            Long attemptId,
            Long responseId,
            Double marks) {

        ExamResponse response = examResponseRepository.findById(responseId)
                .orElseThrow(() ->
                        new IllegalStateException("Response not found"));

        if (!response.getAttemptId().equals(attemptId)) {
            throw new IllegalStateException("Response does not belong to attempt");
        }

        response.setMarksAwarded(marks);
        response.setEvaluationType("MANUAL");

        examResponseRepository.save(response);
    }
}

package com.lms.management.service;

import java.util.List;

import com.lms.management.model.ExamEvaluationLog;

public interface ExamEvaluationLogService {

    ExamEvaluationLog logEvaluationChange(
            Long attemptId,
            Long evaluatorId,
            Double oldScore,
            Double newScore,
            String reason
    );

    List<ExamEvaluationLog> getLogsByAttempt(Long attemptId);
}

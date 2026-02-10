package com.lms.management.service;

import java.util.List;

import com.lms.management.model.ExamEvaluationLog;

public interface ExamEvaluationLogService {

    // existing
    ExamEvaluationLog logEvaluationChange(
            Long attemptId,
            Long evaluatorId,
            Double oldScore,
            Double newScore,
            String reason
    );

    List<ExamEvaluationLog> getLogsByAttempt(Long attemptId);

    // ================= ADD THESE =================

    // 1️⃣ Calculate current total score of attempt
    Double getCurrentScore(Long attemptId);

    // 2️⃣ Update coding response marks
    void evaluateCodingResponse(
            Long attemptId,
            Long responseId,
            Double marks
    );
}

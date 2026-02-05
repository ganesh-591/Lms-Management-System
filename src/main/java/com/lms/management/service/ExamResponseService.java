package com.lms.management.service;

import java.util.List;

import com.lms.management.model.ExamResponse;

public interface ExamResponseService {

    ExamResponse saveOrUpdateResponse(
            Long attemptId,
            Long examQuestionId,
            Long selectedOptionId,
            String descriptiveAnswer,
            String codingSubmissionPath
    );

    void autoEvaluateMcq(Long attemptId);

    List<ExamResponse> getResponsesByAttempt(Long attemptId);

    // ================= MANUAL EVALUATION =================
    ExamResponse evaluateResponse(
            Long attemptId,
            Long responseId,
            Double marks
    );
}

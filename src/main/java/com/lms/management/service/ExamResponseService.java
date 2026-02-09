package com.lms.management.service;

import java.util.List;
import java.util.Map;

import com.lms.management.model.ExamResponse;

public interface ExamResponseService {

    ExamResponse saveOrUpdateResponse(
            Long attemptId,
            Long examQuestionId,
            Long selectedOptionId,
            String descriptiveAnswer,
            String codingSubmissionPath);

    void autoEvaluateMcq(Long attemptId);

    ExamResponse evaluateResponse(
            Long attemptId,
            Long responseId,
            Double marks);

    List<ExamResponse> getResponsesByAttempt(Long attemptId);

    // ✅ ADD THIS (DESCRIPTIVE READ VIEW)
    List<Map<String, Object>> getDescriptiveResponsesForEvaluation(Long attemptId);
    
    List<Map<String, Object>> getCodingResponsesForEvaluation(Long attemptId);
}

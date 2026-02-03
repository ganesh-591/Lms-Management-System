package com.lms.management.service;

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
}

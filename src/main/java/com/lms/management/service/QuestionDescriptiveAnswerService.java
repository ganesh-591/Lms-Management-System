package com.lms.management.service;

import java.util.Map;

public interface QuestionDescriptiveAnswerService {

    Map<String, Object> getByQuestionId(Long questionId);

    Map<String, Object> createOrUpdate(
            Long questionId,
            String answerText,
            String guidelines
    );

    void deleteByQuestionId(Long questionId);
}

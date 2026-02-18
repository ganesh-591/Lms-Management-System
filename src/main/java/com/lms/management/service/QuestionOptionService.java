package com.lms.management.service;

import java.util.List;

import com.lms.management.model.QuestionOption;

public interface QuestionOptionService {

    List<QuestionOption> addOptions(
            Long questionId,
            List<QuestionOption> options);

    List<QuestionOption> getOptionsByQuestion(Long questionId);

    QuestionOption updateOption(
            Long questionId,
            Long optionId,
            QuestionOption request);

    void deleteOption(Long optionId);
}
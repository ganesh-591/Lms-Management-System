package com.lms.management.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.management.model.QuestionOption;
import com.lms.management.repository.QuestionOptionRepository;
import com.lms.management.repository.QuestionRepository;
import com.lms.management.service.QuestionOptionService;

@Service
@Transactional
public class QuestionOptionServiceImpl
        implements QuestionOptionService {

    private final QuestionOptionRepository optionRepository;
    private final QuestionRepository questionRepository;
    

    public QuestionOptionServiceImpl(
            QuestionOptionRepository optionRepository,
            QuestionRepository questionRepository) {

        this.optionRepository = optionRepository;
        this.questionRepository = questionRepository;
    }

    @Override
    public List<QuestionOption> addOptions(
            Long questionId, List<QuestionOption> options) {

        // 🔥 FIRST CHECK QUESTION EXISTS
        if (!questionRepository.existsById(questionId)) {
            throw new IllegalStateException("Question not found");
        }

        for (QuestionOption option : options) {

            if (optionRepository.existsByQuestionIdAndOptionText(
                    questionId, option.getOptionText())) {
                throw new IllegalStateException(
                        "Duplicate option for question");
            }

            option.setQuestionId(questionId);
        }

        return optionRepository.saveAll(options);
    }

    @Override
    public List<QuestionOption> getOptionsByQuestion(Long questionId) {
        return optionRepository.findByQuestionId(questionId);
    }

    @Override
    public QuestionOption updateOption(
            Long questionId,
            Long optionId,
            QuestionOption request) {

        QuestionOption option = optionRepository.findById(optionId)
                .orElseThrow(() ->
                        new IllegalStateException("Option not found"));

        if (!option.getQuestionId().equals(questionId)) {
            throw new IllegalStateException("Invalid question mapping");
        }

        option.setOptionText(request.getOptionText());
        option.setIsCorrect(request.getIsCorrect());

        return optionRepository.save(option);
    }

    @Override
    public void deleteOption(Long optionId) {
        optionRepository.deleteById(optionId);
    }
}
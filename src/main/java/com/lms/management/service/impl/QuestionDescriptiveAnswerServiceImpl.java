package com.lms.management.service.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.management.model.Question;
import com.lms.management.model.QuestionDescriptiveAnswer;
import com.lms.management.repository.QuestionDescriptiveAnswerRepository;
import com.lms.management.repository.QuestionRepository;
import com.lms.management.service.QuestionDescriptiveAnswerService;

@Service
@Transactional
public class QuestionDescriptiveAnswerServiceImpl
        implements QuestionDescriptiveAnswerService {

    private final QuestionDescriptiveAnswerRepository repository;
    private final QuestionRepository questionRepository;

    public QuestionDescriptiveAnswerServiceImpl(
            QuestionDescriptiveAnswerRepository repository,
            QuestionRepository questionRepository) {
        this.repository = repository;
        this.questionRepository = questionRepository;
    }

    // ================= GET MODEL ANSWER =================
    @Override
    public Map<String, Object> getByQuestionId(Long questionId) {

        QuestionDescriptiveAnswer answer = repository
                .findByQuestionId(questionId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Descriptive model answer not found"));

        Map<String, Object> res = new HashMap<>();
        res.put("questionId", answer.getQuestionId());
        res.put("answerText", answer.getAnswerText());
        res.put("guidelines", answer.getGuidelines());

        return res;
    }

    // ================= CREATE / UPDATE =================
    @Override
    public Map<String, Object> createOrUpdate(
            Long questionId,
            String answerText,
            String guidelines) {

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() ->
                        new IllegalStateException("Question not found"));

        if (!"DESCRIPTIVE".equals(question.getQuestionType())) {
            throw new IllegalStateException(
                    "Model answer allowed only for DESCRIPTIVE questions");
        }

        QuestionDescriptiveAnswer answer =
                repository.findByQuestionId(questionId)
                        .orElse(new QuestionDescriptiveAnswer());

        answer.setQuestionId(questionId);
        answer.setAnswerText(answerText);
        answer.setGuidelines(guidelines);

        if (answer.getDescriptiveAnswerId() == null) {
            answer.setCreatedAt(LocalDateTime.now());
        }
        answer.setUpdatedAt(LocalDateTime.now());

        repository.save(answer);

        Map<String, Object> res = new HashMap<>();
        res.put("questionId", questionId);
        res.put("status", "SAVED");

        return res;
    }

    // ================= DELETE =================
    @Override
    public void deleteByQuestionId(Long questionId) {

        QuestionDescriptiveAnswer answer =
                repository.findByQuestionId(questionId)
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Descriptive model answer not found"));

        repository.delete(answer);
    }
}

package com.lms.management.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.management.model.ExamAttempt;
import com.lms.management.model.ExamQuestion;
import com.lms.management.model.ExamResponse;
import com.lms.management.model.QuestionOption;
import com.lms.management.repository.ExamAttemptRepository;
import com.lms.management.repository.ExamQuestionRepository;
import com.lms.management.repository.ExamResponseRepository;
import com.lms.management.repository.QuestionOptionRepository;
import com.lms.management.service.ExamResponseService;

@Service
@Transactional
public class ExamResponseServiceImpl implements ExamResponseService {

    private final ExamResponseRepository examResponseRepository;
    private final ExamAttemptRepository examAttemptRepository;
    private final ExamQuestionRepository examQuestionRepository;
    private final QuestionOptionRepository questionOptionRepository;

    public ExamResponseServiceImpl(
            ExamResponseRepository examResponseRepository,
            ExamAttemptRepository examAttemptRepository,
            ExamQuestionRepository examQuestionRepository,
            QuestionOptionRepository questionOptionRepository) {

        this.examResponseRepository = examResponseRepository;
        this.examAttemptRepository = examAttemptRepository;
        this.examQuestionRepository = examQuestionRepository;
        this.questionOptionRepository = questionOptionRepository;
    }

    // ================= SAVE / UPDATE RESPONSE =================

    @Override
    public ExamResponse saveOrUpdateResponse(
            Long attemptId,
            Long examQuestionId,
            Long selectedOptionId,
            String descriptiveAnswer,
            String codingSubmissionPath) {

        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() ->
                        new IllegalStateException("Attempt not found"));

        if (!"IN_PROGRESS".equals(attempt.getStatus())) {
            throw new IllegalStateException("Cannot modify responses");
        }

        ExamResponse response = examResponseRepository
                .findByAttemptIdAndExamQuestionId(attemptId, examQuestionId)
                .orElse(new ExamResponse());

        response.setAttemptId(attemptId);
        response.setExamQuestionId(examQuestionId);
        response.setSelectedOptionId(selectedOptionId);
        response.setDescriptiveAnswer(descriptiveAnswer);
        response.setCodingSubmissionPath(codingSubmissionPath);

        return examResponseRepository.save(response);
    }

    // ================= AUTO EVALUATE MCQ =================

    @Override
    public void autoEvaluateMcq(Long attemptId) {

        List<ExamResponse> responses =
                examResponseRepository.findByAttemptId(attemptId);

        for (ExamResponse response : responses) {

            if (response.getSelectedOptionId() == null) {
                continue;
            }

            QuestionOption option =
                    questionOptionRepository.findById(
                            response.getSelectedOptionId())
                            .orElseThrow();

            if (option.getIsCorrect()) {

                ExamQuestion examQuestion =
                        examQuestionRepository.findById(
                                response.getExamQuestionId())
                                .orElseThrow();

                response.setMarksAwarded(
                        examQuestion.getMarks());
            } else {
                response.setMarksAwarded(0.0);
            }

            response.setEvaluationType("AUTO");
            examResponseRepository.save(response);
        }
    }
    
    @Override
    public List<ExamResponse> getResponsesByAttempt(Long attemptId) {
        return examResponseRepository.findByAttemptId(attemptId);
    }
}

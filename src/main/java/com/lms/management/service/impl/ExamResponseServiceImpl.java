package com.lms.management.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.management.model.ExamAttempt;
import com.lms.management.model.ExamQuestion;
import com.lms.management.model.ExamResponse;
import com.lms.management.model.ExamSection;
import com.lms.management.model.Question;
import com.lms.management.model.QuestionOption;
import com.lms.management.repository.ExamAttemptRepository;
import com.lms.management.repository.ExamQuestionRepository;
import com.lms.management.repository.ExamResponseRepository;
import com.lms.management.repository.ExamSectionRepository;
import com.lms.management.repository.QuestionOptionRepository;
import com.lms.management.repository.QuestionRepository;
import com.lms.management.service.ExamResponseService;

@Service
@Transactional
public class ExamResponseServiceImpl implements ExamResponseService {

    private final ExamResponseRepository examResponseRepository;
    private final ExamAttemptRepository examAttemptRepository;
    private final ExamQuestionRepository examQuestionRepository;
    private final ExamSectionRepository examSectionRepository;
    private final QuestionOptionRepository questionOptionRepository;
    private final QuestionRepository questionRepository;

    public ExamResponseServiceImpl(
            ExamResponseRepository examResponseRepository,
            ExamAttemptRepository examAttemptRepository,
            ExamQuestionRepository examQuestionRepository,
            ExamSectionRepository examSectionRepository,
            QuestionOptionRepository questionOptionRepository,
            QuestionRepository questionRepository) {

        this.examResponseRepository = examResponseRepository;
        this.examAttemptRepository = examAttemptRepository;
        this.examQuestionRepository = examQuestionRepository;
        this.examSectionRepository = examSectionRepository;
        this.questionOptionRepository = questionOptionRepository;
        this.questionRepository = questionRepository;
    }

    // ================= SAVE / UPDATE RESPONSE =================
    @Override
    public ExamResponse saveOrUpdateResponse(
            Long attemptId,
            Long examQuestionId,
            Long selectedOptionId,
            String descriptiveAnswer,
            String codingSubmissionCode) {

        // 1️⃣ Fetch attempt
        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new IllegalStateException("Attempt not found"));

        if (!"IN_PROGRESS".equals(attempt.getStatus())) {
            throw new IllegalStateException("Cannot modify responses");
        }

        // 2️⃣ Fetch exam question
        ExamQuestion examQuestion = examQuestionRepository
                .findById(examQuestionId)
                .orElseThrow(() -> new IllegalStateException("Exam question not found"));

        // 3️⃣ Fetch exam section
        ExamSection examSection = examSectionRepository
                .findById(examQuestion.getExamSectionId())
                .orElseThrow(() -> new IllegalStateException("Exam section not found"));

        // 4️⃣ Validate ownership
        if (!examSection.getExamId().equals(attempt.getExamId())) {
            throw new IllegalStateException("Question does not belong to this exam");
        }

        // 5️⃣ Fetch response
        ExamResponse response = examResponseRepository
                .findByAttemptIdAndExamQuestionId(attemptId, examQuestionId)
                .orElseThrow(() ->
                        new IllegalStateException("Question not assigned to this attempt"));

        // 6️⃣ Fetch actual question
        Question question = questionRepository
                .findById(examQuestion.getQuestionId())
                .orElseThrow();

        // 🔒 STRICT TYPE-BASED SAVE
        switch (question.getQuestionType()) {

            case "MCQ":
                response.setSelectedOptionId(selectedOptionId);
                response.setDescriptiveAnswer(null);
                response.setCodingSubmissionCode(null);
                break;

            case "DESCRIPTIVE":
                response.setDescriptiveAnswer(descriptiveAnswer);
                response.setSelectedOptionId(null);
                response.setCodingSubmissionCode(null);
                break;

            case "CODING":
                response.setCodingSubmissionCode(codingSubmissionCode);
                response.setSelectedOptionId(null);
                response.setDescriptiveAnswer(null);
                break;

            default:
                throw new IllegalStateException("Unsupported question type");
        }

        return examResponseRepository.save(response);
    }

    // ================= AUTO EVALUATE MCQ =================
    @Override
    public void autoEvaluateMcq(Long attemptId) {

        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new IllegalStateException("Attempt not found"));

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

            ExamQuestion examQuestion =
                    examQuestionRepository.findById(
                            response.getExamQuestionId())
                            .orElseThrow();

            response.setMarksAwarded(
                    option.getIsCorrect() ? examQuestion.getMarks() : 0.0);

            response.setEvaluationType("AUTO");
            examResponseRepository.save(response);
        }
    }

    // ================= MANUAL EVALUATION =================
    @Override
    public ExamResponse evaluateResponse(
            Long attemptId,
            Long responseId,
            Double marks) {

        ExamResponse response = examResponseRepository.findById(responseId)
                .orElseThrow(() -> new IllegalStateException("Response not found"));

        if (!response.getAttemptId().equals(attemptId)) {
            throw new IllegalStateException("Response does not belong to this attempt");
        }

        response.setMarksAwarded(marks);
        response.setEvaluationType("MANUAL");

        return examResponseRepository.save(response);
    }

    // ================= GET RESPONSES =================
    @Override
    public List<ExamResponse> getResponsesByAttempt(Long attemptId) {
        return examResponseRepository.findByAttemptId(attemptId);
    }

    // ================= DESCRIPTIVE VIEW =================
    @Override
    public List<Map<String, Object>> getDescriptiveResponsesForEvaluation(Long attemptId) {

        List<ExamResponse> responses =
                examResponseRepository.findByAttemptId(attemptId);

        List<Map<String, Object>> result = new ArrayList<>();

        for (ExamResponse response : responses) {

            ExamQuestion examQuestion =
                    examQuestionRepository.findById(
                            response.getExamQuestionId())
                            .orElseThrow();

            Question question =
                    questionRepository.findById(
                            examQuestion.getQuestionId())
                            .orElseThrow();

            if (!"DESCRIPTIVE".equals(question.getQuestionType())) {
                continue;
            }

            Map<String, Object> row = new HashMap<>();
            row.put("responseId", response.getResponseId());
            row.put("questionText", question.getQuestionText());
            row.put("maxMarks", examQuestion.getMarks());
            row.put("studentAnswer", response.getDescriptiveAnswer());
            row.put("marksAwarded", response.getMarksAwarded());

            result.add(row);
        }

        return result;
    }

    // ================= CODING VIEW =================
    @Override
    public List<Map<String, Object>> getCodingResponsesForEvaluation(Long attemptId) {

        List<ExamResponse> responses =
                examResponseRepository.findByAttemptId(attemptId);

        List<Map<String, Object>> result = new ArrayList<>();

        for (ExamResponse response : responses) {

            if (response.getCodingSubmissionCode() == null) {
                continue;
            }

            ExamQuestion examQuestion =
                    examQuestionRepository.findById(
                            response.getExamQuestionId())
                            .orElseThrow();

            Question question =
                    questionRepository.findById(
                            examQuestion.getQuestionId())
                            .orElseThrow();

            if (!"CODING".equals(question.getQuestionType())) {
                continue;
            }

            Map<String, Object> row = new HashMap<>();
            row.put("responseId", response.getResponseId());
            row.put("questionText", question.getQuestionText());
            row.put("maxMarks", examQuestion.getMarks());
            row.put("submissionCode", response.getCodingSubmissionCode());
            row.put("marksAwarded", response.getMarksAwarded());

            result.add(row);
        }

        return result;
    }
}
package com.lms.management.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.management.model.Exam;
import com.lms.management.model.ExamQuestion;
import com.lms.management.model.Question;
import com.lms.management.repository.ExamQuestionRepository;
import com.lms.management.repository.ExamRepository;
import com.lms.management.repository.QuestionOptionRepository;
import com.lms.management.repository.QuestionRepository;
import com.lms.management.service.ExamQuestionService;

@Service
@Transactional
public class ExamQuestionServiceImpl implements ExamQuestionService {

    private final ExamQuestionRepository examQuestionRepository;
    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final QuestionOptionRepository questionOptionRepository;

    public ExamQuestionServiceImpl(
            ExamQuestionRepository examQuestionRepository,
            ExamRepository examRepository,
            QuestionRepository questionRepository,
            QuestionOptionRepository questionOptionRepository
    ) {
        this.examQuestionRepository = examQuestionRepository;
        this.examRepository = examRepository;
        this.questionRepository = questionRepository;
        this.questionOptionRepository = questionOptionRepository;
    }

    @Override
    public List<ExamQuestion> addQuestions(
            Long examId, List<ExamQuestion> questions) {

        Exam exam = examRepository.findById(examId)
                .orElseThrow(() ->
                        new IllegalStateException("Exam not found"));

        if (!"DRAFT".equals(exam.getStatus())) {
            throw new IllegalStateException(
                    "Questions can be added only in DRAFT");
        }

        for (ExamQuestion q : questions) {

            if (examQuestionRepository
                    .existsByExamIdAndQuestionId(
                            examId, q.getQuestionId())) {
                throw new IllegalStateException(
                        "Duplicate question in exam");
            }

            q.setExamId(examId);
        }

        return examQuestionRepository.saveAll(questions);
    }

    @Override
    public List<ExamQuestion> getQuestionsByExam(Long examId) {
        return examQuestionRepository
                .findByExamIdOrderByQuestionOrderAsc(examId);
    }

    @Override
    public ExamQuestion updateExamQuestion(
            Long examId,
            Long examQuestionId,
            ExamQuestion request) {

        ExamQuestion existing = examQuestionRepository.findById(examQuestionId)
                .orElseThrow(() ->
                        new IllegalStateException("Exam question not found"));

        if (!existing.getExamId().equals(examId)) {
            throw new IllegalStateException("Invalid exam mapping");
        }

        existing.setMarks(request.getMarks());
        existing.setQuestionOrder(request.getQuestionOrder());

        return examQuestionRepository.save(existing);
    }

    @Override
    public void removeExamQuestion(Long examQuestionId) {
        examQuestionRepository.deleteById(examQuestionId);
    }

    @Override
    public List<Map<String, Object>> getExamQuestionsForStudent(Long examId) {

        List<ExamQuestion> examQuestions =
                examQuestionRepository
                        .findByExamIdOrderByQuestionOrderAsc(examId);

        List<Map<String, Object>> result = new ArrayList<>();

        for (ExamQuestion eq : examQuestions) {

            Question question = questionRepository
                    .findById(eq.getQuestionId())
                    .orElseThrow();

            Map<String, Object> q = new HashMap<>();
            q.put("examQuestionId", eq.getExamQuestionId());
            q.put("questionId", question.getQuestionId());
            q.put("questionText", question.getQuestionText());
            q.put("questionType", question.getQuestionType());
            q.put("marks", eq.getMarks());
            q.put("questionOrder", eq.getQuestionOrder());

            // ================= MCQ =================
            if ("MCQ".equalsIgnoreCase(question.getQuestionType())
                    || "QUIZ".equalsIgnoreCase(question.getQuestionType())) {

                q.put("options",
                        questionOptionRepository
                                .findByQuestionId(question.getQuestionId())
                                .stream()
                                .map(opt -> {
                                    Map<String, Object> option = new HashMap<>();
                                    option.put("optionId", opt.getOptionId());
                                    option.put("optionText", opt.getOptionText());
                                    return option;
                                })
                                .toList()
                );
            }

            result.add(q);
        }

        return result;
    }
}

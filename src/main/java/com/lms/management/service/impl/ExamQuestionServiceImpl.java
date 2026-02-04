package com.lms.management.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.management.model.Exam;
import com.lms.management.model.ExamQuestion;
import com.lms.management.repository.ExamQuestionRepository;
import com.lms.management.repository.ExamRepository;
import com.lms.management.service.ExamQuestionService;

@Service
@Transactional
public class ExamQuestionServiceImpl implements ExamQuestionService {

    private final ExamQuestionRepository examQuestionRepository;
    private final ExamRepository examRepository;

    public ExamQuestionServiceImpl(
            ExamQuestionRepository examQuestionRepository,
            ExamRepository examRepository) {
        this.examQuestionRepository = examQuestionRepository;
        this.examRepository = examRepository;
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
}

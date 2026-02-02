package com.lms.management.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.management.exception.ResourceNotFoundException;
import com.lms.management.model.Exam;
import com.lms.management.repository.ExamRepository;
import com.lms.management.service.ExamService;

@Service
@Transactional
public class ExamServiceImpl implements ExamService {

    private final ExamRepository examRepository;

    public ExamServiceImpl(ExamRepository examRepository) {
        this.examRepository = examRepository;
    }

    @Override
    public Exam createExam(Exam exam) {

        // Basic validations
        if (exam.getAttemptsAllowed() == null || exam.getAttemptsAllowed() <= 0) {
            throw new IllegalArgumentException("Attempts allowed must be greater than 0");
        }

        if (exam.getPassPercentage() == null ||
            exam.getPassPercentage() < 0 ||
            exam.getPassPercentage() > 100) {
            throw new IllegalArgumentException("Pass percentage must be between 0 and 100");
        }

        // Status is forced by backend
        exam.setStatus("DRAFT");

        return examRepository.save(exam);
    }

    @Override
    public Exam publishExam(Long examId) {

        Exam exam = getExamById(examId);

        if (!"DRAFT".equals(exam.getStatus())) {
            throw new IllegalStateException("Only DRAFT exams can be published");
        }

        exam.setStatus("PUBLISHED");
        return examRepository.save(exam);
    }

    @Override
    public Exam closeExam(Long examId) {

        Exam exam = getExamById(examId);

        if ("CLOSED".equals(exam.getStatus())) {
            throw new IllegalStateException("Exam already closed");
        }

        exam.setStatus("CLOSED");
        return examRepository.save(exam);
    }

    @Override
    public Exam getExamById(Long examId) {
        return examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id: " + examId));
    }

    @Override
    public List<Exam> getExamsByCourseId(Long courseId) {
        return examRepository.findByCourseId(courseId);
    }

    @Override
    public List<Exam> getExamsByBatchId(Long batchId) {
        return examRepository.findByBatchId(batchId);
    }
}

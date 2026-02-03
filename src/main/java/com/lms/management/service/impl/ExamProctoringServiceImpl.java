package com.lms.management.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.management.exception.ResourceNotFoundException;
import com.lms.management.model.Exam;
import com.lms.management.model.ExamProctoring;
import com.lms.management.repository.ExamProctoringRepository;
import com.lms.management.repository.ExamRepository;
import com.lms.management.service.ExamProctoringService;

@Service
@Transactional
public class ExamProctoringServiceImpl implements ExamProctoringService {

    private final ExamProctoringRepository examProctoringRepository;
    private final ExamRepository examRepository;

    public ExamProctoringServiceImpl(
            ExamProctoringRepository examProctoringRepository,
            ExamRepository examRepository) {
        this.examProctoringRepository = examProctoringRepository;
        this.examRepository = examRepository;
    }

    @Override
    public ExamProctoring saveProctoring(Long examId, ExamProctoring proctoring) {

        Exam exam = examRepository.findById(examId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Exam not found with id: " + examId));

        // 🔒 ENTERPRISE RULE: no mutation after publish
        if (!"DRAFT".equals(exam.getStatus())) {
            throw new IllegalStateException(
                    "Exam proctoring settings can be modified only when exam is in DRAFT state");
        }

        proctoring.setExamId(examId);

        // UPSERT (idempotent & safe)
        return examProctoringRepository.findByExamId(examId)
                .map(existing -> {
                    proctoring.setProctoringId(existing.getProctoringId());
                    return examProctoringRepository.save(proctoring);
                })
                .orElseGet(() -> examProctoringRepository.save(proctoring));
    }

    @Override
    public ExamProctoring getProctoringByExamId(Long examId) {
        return examProctoringRepository.findByExamId(examId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Exam proctoring not found for examId: " + examId));
    }
}

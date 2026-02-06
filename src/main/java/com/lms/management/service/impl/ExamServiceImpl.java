package com.lms.management.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.management.exception.ResourceNotFoundException;
import com.lms.management.model.Batch;
import com.lms.management.model.Exam;
import com.lms.management.repository.BatchRepository;
import com.lms.management.repository.CourseRepository;
import com.lms.management.repository.ExamRepository;
import com.lms.management.service.ExamService;

@Service
@Transactional
public class ExamServiceImpl implements ExamService {

    private final ExamRepository examRepository;
    private final CourseRepository courseRepository;
    private final BatchRepository batchRepository;

    public ExamServiceImpl(
            ExamRepository examRepository,
            CourseRepository courseRepository,
            BatchRepository batchRepository) {

        this.examRepository = examRepository;
        this.courseRepository = courseRepository;
        this.batchRepository = batchRepository;
    }

    @Override
    public Exam createExam(Exam exam) {

        // 1️⃣ Validate course exists
        courseRepository.findById(exam.getCourseId())
                .orElseThrow(() ->
                        new IllegalStateException("Invalid courseId"));

        // 2️⃣ Validate batch exists
        Batch batch = batchRepository.findById(exam.getBatchId())
                .orElseThrow(() ->
                        new IllegalStateException("Invalid batchId"));

        // 3️⃣ Validate batch belongs to course
        if (!batch.getCourseId().equals(exam.getCourseId())) {
            throw new IllegalStateException(
                    "Batch does not belong to given course");
        }

        exam.setStatus("DRAFT");
        exam.setIsDeleted(false);

        return examRepository.save(exam);
    }
    
    @Override
    public List<Exam> getAllExams() {
        return examRepository.findAll();
    }
    
    
    @Override
    public Exam publishExam(Long examId) {
        Exam exam = getExamById(examId);
        exam.setStatus("PUBLISHED");
        return examRepository.save(exam);
    }

    @Override
    public Exam closeExam(Long examId) {
        Exam exam = getExamById(examId);
        exam.setStatus("CLOSED");
        return examRepository.save(exam);
    }

    @Override
    public Exam getExamById(Long examId) {
        return examRepository.findByExamIdAndIsDeletedFalse(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));
    }

    @Override
    public List<Exam> getExamsByCourseId(Long courseId) {
        return examRepository.findByCourseIdAndIsDeletedFalse(courseId);
    }

    @Override
    public List<Exam> getExamsByBatchId(Long batchId) {
        return examRepository.findByBatchIdAndIsDeletedFalse(batchId);
    }

    // ================= DELETE LOGIC =================

    @Override
    public void softDeleteExam(Long examId) {
        Exam exam = getExamById(examId);
        exam.setIsDeleted(true);
        examRepository.save(exam);
    }

    @Override
    public void restoreExam(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));
        exam.setIsDeleted(false);
        examRepository.save(exam);
    }

    @Override
    public void hardDeleteExam(Long examId) {
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));
        examRepository.delete(exam);
    }
}

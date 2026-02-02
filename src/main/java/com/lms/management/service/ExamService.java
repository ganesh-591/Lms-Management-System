package com.lms.management.service;

import java.util.List;

import com.lms.management.model.Exam;

public interface ExamService {

    // Create exam (DRAFT)
    Exam createExam(Exam exam);

    // Publish exam
    Exam publishExam(Long examId);

    // Close exam
    Exam closeExam(Long examId);

    // Get exam by id
    Exam getExamById(Long examId);

    // Get exams by course
    List<Exam> getExamsByCourseId(Long courseId);

    // Get exams by batch
    List<Exam> getExamsByBatchId(Long batchId);
}

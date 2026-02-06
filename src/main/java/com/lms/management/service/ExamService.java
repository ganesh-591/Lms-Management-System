package com.lms.management.service;

import java.util.List;

import com.lms.management.model.Exam;

public interface ExamService {

    Exam createExam(Exam exam);

    Exam publishExam(Long examId);

    Exam closeExam(Long examId);

    Exam getExamById(Long examId);
    
    List<Exam> getAllExams();

    List<Exam> getExamsByCourseId(Long courseId);

    List<Exam> getExamsByBatchId(Long batchId);

    // DELETE OPERATIONS
    void softDeleteExam(Long examId);

    void restoreExam(Long examId);

    void hardDeleteExam(Long examId);
}

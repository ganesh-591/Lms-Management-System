package com.lms.management.service;

import com.lms.management.model.ExamGrading;

public interface ExamGradingService {

    ExamGrading saveGrading(Long examId, ExamGrading grading);

    ExamGrading getGradingByExamId(Long examId);
}

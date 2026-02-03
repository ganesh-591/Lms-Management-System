package com.lms.management.service;

import com.lms.management.model.ExamProctoring;

public interface ExamProctoringService {

    ExamProctoring saveProctoring(Long examId, ExamProctoring proctoring);

    ExamProctoring getProctoringByExamId(Long examId);
}

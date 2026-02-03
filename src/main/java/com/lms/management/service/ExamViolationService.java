package com.lms.management.service;

import com.lms.management.model.ExamViolation;

public interface ExamViolationService {

    ExamViolation recordViolation(
            Long attemptId, String violationType);

    long getViolationCount(Long attemptId);
}

package com.lms.management.service;

import java.util.List;

import com.lms.management.model.ExamViolation;

public interface ExamViolationService {

    ExamViolation recordViolation(
            Long attemptId, String violationType);

    long getViolationCount(Long attemptId);
    
    List<ExamViolation> getViolationsByAttempt(Long attemptId);
}

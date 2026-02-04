package com.lms.management.service;

import com.lms.management.model.ExamAttempt;

public interface ExamAttemptService {

    ExamAttempt startAttempt(Long examId, Long studentId);

    ExamAttempt submitAttempt(Long attemptId, Long studentId);

    ExamAttempt autoSubmitAttempt(Long attemptId, Long studentId);

    ExamAttempt getAttemptById(Long attemptId, Long studentId);
}

package com.lms.management.service;

import com.lms.management.model.ExamAttempt;

public interface ExamAttemptService {

    ExamAttempt startAttempt(Long examId, Long studentId);

    ExamAttempt submitAttempt(Long attemptId);

    ExamAttempt autoSubmitAttempt(Long attemptId);
}

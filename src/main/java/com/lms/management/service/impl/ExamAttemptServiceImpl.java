package com.lms.management.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.management.model.Exam;
import com.lms.management.model.ExamAttempt;
import com.lms.management.model.ExamSettings;
import com.lms.management.repository.ExamAttemptRepository;
import com.lms.management.repository.ExamRepository;
import com.lms.management.repository.ExamSettingsRepository;
import com.lms.management.service.ExamAttemptService;

@Service
@Transactional
public class ExamAttemptServiceImpl implements ExamAttemptService {

    private final ExamAttemptRepository examAttemptRepository;
    private final ExamRepository examRepository;
    private final ExamSettingsRepository examSettingsRepository;

    public ExamAttemptServiceImpl(
            ExamAttemptRepository examAttemptRepository,
            ExamRepository examRepository,
            ExamSettingsRepository examSettingsRepository) {
        this.examAttemptRepository = examAttemptRepository;
        this.examRepository = examRepository;
        this.examSettingsRepository = examSettingsRepository;
    }

    // ================= START ATTEMPT =================

    @Override
    public ExamAttempt startAttempt(Long examId, Long studentId) {

        Exam exam = examRepository.findById(examId)
                .orElseThrow(() ->
                        new IllegalStateException("Exam not found"));

        if (!"PUBLISHED".equals(exam.getStatus())) {
            throw new IllegalStateException(
                    "Exam is not available for attempt");
        }

        // ❌ Only one IN_PROGRESS attempt allowed
        if (examAttemptRepository.existsByExamIdAndStudentIdAndStatus(
                examId, studentId, "IN_PROGRESS")) {
            throw new IllegalStateException(
                    "An active attempt already exists");
        }

        int usedAttempts =
                examAttemptRepository.countByExamIdAndStudentId(
                        examId, studentId);

        // ✅ FETCH ATTEMPTS FROM EXAM_SETTINGS (DB3)
        ExamSettings settings = examSettingsRepository
                .findByExamId(examId)
                .orElseThrow(() ->
                        new IllegalStateException("Exam settings not configured"));

        if (usedAttempts >= settings.getAttemptsAllowed()) {
            throw new IllegalStateException(
                    "No attempts left for this exam");
        }

        ExamAttempt attempt = new ExamAttempt();
        attempt.setExamId(examId);
        attempt.setStudentId(studentId);
        attempt.setAttemptNumber(usedAttempts + 1);
        attempt.setStartTime(LocalDateTime.now());
        attempt.setStatus("IN_PROGRESS");

        return examAttemptRepository.save(attempt);
    }

    // ================= MANUAL SUBMIT =================

    @Override
    public ExamAttempt submitAttempt(Long attemptId) {

        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() ->
                        new IllegalStateException("Attempt not found"));

        if (!"IN_PROGRESS".equals(attempt.getStatus())) {
            throw new IllegalStateException(
                    "Attempt cannot be submitted");
        }

        attempt.setStatus("SUBMITTED");
        attempt.setEndTime(LocalDateTime.now());

        return examAttemptRepository.save(attempt);
    }

    // ================= AUTO SUBMIT =================

    @Override
    public ExamAttempt autoSubmitAttempt(Long attemptId) {

        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() ->
                        new IllegalStateException("Attempt not found"));

        if (!"IN_PROGRESS".equals(attempt.getStatus())) {
            return attempt;
        }

        attempt.setStatus("AUTO_SUBMITTED");
        attempt.setEndTime(LocalDateTime.now());

        return examAttemptRepository.save(attempt);
    }
}

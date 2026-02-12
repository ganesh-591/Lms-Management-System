package com.lms.management.service.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.management.model.Exam;
import com.lms.management.model.ExamAttempt;
import com.lms.management.model.ExamGrading;
import com.lms.management.model.ExamQuestion;
import com.lms.management.model.ExamResponse;
import com.lms.management.model.ExamSettings;
import com.lms.management.repository.ExamAttemptRepository;
import com.lms.management.repository.ExamGradingRepository;
import com.lms.management.repository.ExamQuestionRepository;
import com.lms.management.repository.ExamRepository;
import com.lms.management.repository.ExamResponseRepository;
import com.lms.management.repository.ExamSettingsRepository;
import com.lms.management.service.CodingExecutionService;
import com.lms.management.service.ExamAttemptService;
import com.lms.management.service.ExamResponseService;

@Service
@Transactional
public class ExamAttemptServiceImpl implements ExamAttemptService {

    private final ExamAttemptRepository examAttemptRepository;
    private final ExamRepository examRepository;
    private final ExamSettingsRepository examSettingsRepository;
    private final ExamQuestionRepository examQuestionRepository;
    private final ExamResponseService examResponseService;
    private final ExamResponseRepository examResponseRepository;
    private final ExamGradingRepository examGradingRepository;
    private final CodingExecutionService codingExecutionService;

    public ExamAttemptServiceImpl(
            ExamAttemptRepository examAttemptRepository,
            ExamRepository examRepository,
            ExamSettingsRepository examSettingsRepository,
            ExamQuestionRepository examQuestionRepository,
            ExamResponseService examResponseService,
            ExamResponseRepository examResponseRepository,
            ExamGradingRepository examGradingRepository,
            CodingExecutionService codingExecutionService) {

        this.examAttemptRepository = examAttemptRepository;
        this.examRepository = examRepository;
        this.examSettingsRepository = examSettingsRepository;
        this.examQuestionRepository = examQuestionRepository;
        this.examResponseService = examResponseService;
        this.examResponseRepository = examResponseRepository;
        this.examGradingRepository = examGradingRepository;
        this.codingExecutionService = codingExecutionService;
    }

    // ================= TIMEOUT CHECK =================
    private void checkAndAutoSubmitIfExpired(ExamAttempt attempt) {

        if (!"IN_PROGRESS".equals(attempt.getStatus())) return;

        Exam exam = examRepository.findById(attempt.getExamId())
                .orElseThrow(() -> new IllegalStateException("Exam not found"));

        LocalDateTime expiry =
                attempt.getStartTime().plusMinutes(exam.getDurationMinutes());

        if (LocalDateTime.now().isAfter(expiry)) {
            autoSubmitAttempt(attempt.getAttemptId(), attempt.getStudentId());
        }
    }

    // ================= START ATTEMPT =================
    @Override
    public ExamAttempt startAttempt(Long examId, Long studentId) {

        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new IllegalStateException("Exam not found"));

        if (!"PUBLISHED".equals(exam.getStatus())) {
            throw new IllegalStateException("Exam not available");
        }

        List<ExamQuestion> questions =
                examQuestionRepository.findByExamIdOrderByQuestionOrderAsc(examId);

        if (questions.isEmpty()) {
            throw new IllegalStateException("No questions in exam");
        }

        if (examAttemptRepository.existsByExamIdAndStudentIdAndStatus(
                examId, studentId, "IN_PROGRESS")) {
            throw new IllegalStateException("Active attempt exists");
        }

        int usedAttempts =
                examAttemptRepository.countByExamIdAndStudentId(examId, studentId);

        ExamSettings settings = examSettingsRepository
                .findByExamId(examId)
                .orElseThrow(() -> new IllegalStateException("Settings missing"));

        if (usedAttempts >= settings.getAttemptsAllowed()) {
            throw new IllegalStateException("No attempts left");
        }

        ExamAttempt attempt = new ExamAttempt();
        attempt.setExamId(examId);
        attempt.setStudentId(studentId);
        attempt.setAttemptNumber(usedAttempts + 1);
        attempt.setStartTime(LocalDateTime.now());
        attempt.setStatus("IN_PROGRESS");

        attempt = examAttemptRepository.save(attempt);

        // Bind responses
        for (ExamQuestion q : questions) {
            ExamResponse r = new ExamResponse();
            r.setAttemptId(attempt.getAttemptId());
            r.setExamQuestionId(q.getExamQuestionId());
            r.setMarksAwarded(0.0);
            examResponseRepository.save(r);
        }

        return attempt;
    }

    // ================= SUBMIT =================
    @Override
    public ExamAttempt submitAttempt(Long attemptId, Long studentId) {

        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new IllegalStateException("Attempt not found"));

        if (!attempt.getStudentId().equals(studentId)) {
            throw new AccessDeniedException("Not your attempt");
        }

        checkAndAutoSubmitIfExpired(attempt);

        if (!"IN_PROGRESS".equals(attempt.getStatus())) {
            throw new IllegalStateException("Cannot submit");
        }

        attempt.setStatus("SUBMITTED");
        attempt.setEndTime(LocalDateTime.now());

        return examAttemptRepository.save(attempt);
    }

    // ================= AUTO SUBMIT =================
    @Override
    public ExamAttempt autoSubmitAttempt(Long attemptId, Long studentId) {

        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new IllegalStateException("Attempt not found"));

        if (!attempt.getStudentId().equals(studentId)) {
            throw new AccessDeniedException("Not your attempt");
        }

        if (!"IN_PROGRESS".equals(attempt.getStatus())) return attempt;

        attempt.setStatus("AUTO_SUBMITTED");
        attempt.setEndTime(LocalDateTime.now());

        return examAttemptRepository.save(attempt);
    }

    // ================= ENTERPRISE EVALUATION =================
    @Override
    public void evaluateAttempt(Long attemptId) {

        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new IllegalStateException("Attempt not found"));

        if (!"SUBMITTED".equals(attempt.getStatus())
                && !"AUTO_SUBMITTED".equals(attempt.getStatus())) {
            throw new IllegalStateException("Not ready for evaluation");
        }

        // 1️⃣ MCQ
        examResponseService.autoEvaluateMcq(attemptId);

        // 2️⃣ CODING
        List<ExamResponse> responses =
                examResponseRepository.findByAttemptId(attemptId);

        for (ExamResponse r : responses) {
            if (r.getCodingSubmissionCode() != null
                    && !r.getCodingSubmissionCode().isBlank()) {

                codingExecutionService.runSubmission(r.getResponseId());
            }
        }

        // 3️⃣ Recalculate total score
        responses = examResponseRepository.findByAttemptId(attemptId);

        double totalScore = responses.stream()
                .map(ExamResponse::getMarksAwarded)
                .filter(m -> m != null)
                .mapToDouble(Double::doubleValue)
                .sum();

        attempt.setScore(totalScore);
        attempt.setStatus("EVALUATED");

        examAttemptRepository.save(attempt);
    }

    // ================= RESULT =================
    @Override
    public Object getResult(Long attemptId, Long studentId) {

        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new IllegalStateException("Attempt not found"));

        if (!attempt.getStudentId().equals(studentId)) {
            throw new AccessDeniedException("Not your attempt");
        }

        if (!"EVALUATED".equals(attempt.getStatus())) {
            throw new IllegalStateException("Result not available");
        }

        Exam exam = examRepository.findById(attempt.getExamId())
                .orElseThrow();

        ExamGrading grading = examGradingRepository
                .findByExamId(exam.getExamId())
                .orElseThrow();

        double percentage =
                (attempt.getScore() / exam.getTotalMarks()) * 100;

        boolean passed =
                percentage >= exam.getPassPercentage();

        List<ExamResponse> responses =
                examResponseRepository.findByAttemptId(attemptId);

        long attempted = responses.stream()
                .filter(r ->
                        r.getSelectedOptionId() != null ||
                        r.getDescriptiveAnswer() != null ||
                        r.getCodingSubmissionCode() != null)
                .count();

        Map<String, Object> result = new HashMap<>();
        result.put("attemptId", attemptId);
        result.put("attemptedCount", attempted);
        result.put("percentage", percentage);
        result.put("passed", passed);

        if (grading.getShowResult()) {
            result.put("score", attempt.getScore());
        }

        return result;
    }

    @Override
    public ExamAttempt getAttemptById(Long attemptId, Long studentId) {

        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new IllegalStateException("Attempt not found"));

        if (!attempt.getStudentId().equals(studentId)) {
            throw new AccessDeniedException("Not your attempt");
        }

        return attempt;
    }
}

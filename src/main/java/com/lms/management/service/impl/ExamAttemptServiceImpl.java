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

    public ExamAttemptServiceImpl(
            ExamAttemptRepository examAttemptRepository,
            ExamRepository examRepository,
            ExamSettingsRepository examSettingsRepository,
            ExamQuestionRepository examQuestionRepository,
            ExamResponseService examResponseService,
            ExamResponseRepository examResponseRepository,
            ExamGradingRepository examGradingRepository) {

        this.examAttemptRepository = examAttemptRepository;
        this.examRepository = examRepository;
        this.examSettingsRepository = examSettingsRepository;
        this.examQuestionRepository = examQuestionRepository;
        this.examResponseService = examResponseService;
        this.examResponseRepository = examResponseRepository;
        this.examGradingRepository = examGradingRepository;
    }

    // ================= TIMEOUT CHECK =================
    private void checkAndAutoSubmitIfExpired(ExamAttempt attempt) {

        if (!"IN_PROGRESS".equals(attempt.getStatus())) {
            return;
        }

        Exam exam = examRepository.findById(attempt.getExamId())
                .orElseThrow(() -> new IllegalStateException("Exam not found"));

        LocalDateTime expiryTime =
                attempt.getStartTime().plusMinutes(exam.getDurationMinutes());

        if (LocalDateTime.now().isAfter(expiryTime)) {
            autoSubmitAttempt(attempt.getAttemptId(), attempt.getStudentId());
        }
    }

    // ================= START ATTEMPT =================
    @Override
    public ExamAttempt startAttempt(Long examId, Long studentId) {

        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new IllegalStateException("Exam not found"));

        if (!"PUBLISHED".equals(exam.getStatus())) {
            throw new IllegalStateException("Exam is not available for attempt");
        }

        List<ExamQuestion> questions =
                examQuestionRepository.findByExamIdOrderByQuestionOrderAsc(examId);

        if (questions.isEmpty()) {
            throw new IllegalStateException("Cannot start exam without questions");
        }

        if (examAttemptRepository.existsByExamIdAndStudentIdAndStatus(
                examId, studentId, "IN_PROGRESS")) {
            throw new IllegalStateException("An active attempt already exists");
        }

        int usedAttempts =
                examAttemptRepository.countByExamIdAndStudentId(examId, studentId);

        ExamSettings settings = examSettingsRepository
                .findByExamId(examId)
                .orElseThrow(() ->
                        new IllegalStateException("Exam settings not configured"));

        if (usedAttempts >= settings.getAttemptsAllowed()) {
            throw new IllegalStateException("No attempts left for this exam");
        }

        ExamAttempt attempt = new ExamAttempt();
        attempt.setExamId(examId);
        attempt.setStudentId(studentId);
        attempt.setAttemptNumber(usedAttempts + 1);
        attempt.setStartTime(LocalDateTime.now());
        attempt.setStatus("IN_PROGRESS");

        attempt = examAttemptRepository.save(attempt);

        // 🔒 Bind questions to attempt
        for (ExamQuestion question : questions) {
            ExamResponse response = new ExamResponse();
            response.setAttemptId(attempt.getAttemptId());
            response.setExamQuestionId(question.getExamQuestionId());
            response.setMarksAwarded(0.0);
            response.setEvaluationType(null);
            examResponseRepository.save(response);
        }

        return attempt;
    }

    // ================= MANUAL SUBMIT =================
    @Override
    public ExamAttempt submitAttempt(Long attemptId, Long studentId) {

        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new IllegalStateException("Attempt not found"));

        if (!attempt.getStudentId().equals(studentId)) {
            throw new AccessDeniedException("Attempt does not belong to this student");
        }

        checkAndAutoSubmitIfExpired(attempt);

        if (!"IN_PROGRESS".equals(attempt.getStatus())) {
            throw new IllegalStateException("Attempt cannot be submitted");
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
            throw new AccessDeniedException("Attempt does not belong to this student");
        }

        if (!"IN_PROGRESS".equals(attempt.getStatus())) {
            return attempt;
        }

        attempt.setStatus("AUTO_SUBMITTED");
        attempt.setEndTime(LocalDateTime.now());

        return examAttemptRepository.save(attempt);
    }

    // ================= EVALUATE ATTEMPT =================
    @Override
    public void evaluateAttempt(Long attemptId) {

        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new IllegalStateException("Attempt not found"));

        if (!"SUBMITTED".equals(attempt.getStatus())
                && !"AUTO_SUBMITTED".equals(attempt.getStatus())) {
            throw new IllegalStateException("Attempt not ready for evaluation");
        }

        examResponseService.autoEvaluateMcq(attemptId);

        List<ExamResponse> responses =
                examResponseRepository.findByAttemptId(attemptId);

        double totalScore = responses.stream()
                .map(ExamResponse::getMarksAwarded)
                .filter(m -> m != null)
                .mapToDouble(Double::doubleValue)
                .sum();

        attempt.setScore(totalScore);
        attempt.setStatus("EVALUATED");

        examAttemptRepository.save(attempt);
    }

    // ================= GET RESULT =================
    @Override
    public Object getResult(Long attemptId, Long studentId) {

        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new IllegalStateException("Attempt not found"));

        if (!attempt.getStudentId().equals(studentId)) {
            throw new AccessDeniedException("Attempt does not belong to this student");
        }

        checkAndAutoSubmitIfExpired(attempt);

        if (!"EVALUATED".equals(attempt.getStatus())) {
            throw new IllegalStateException("Result not available yet");
        }

        Exam exam = examRepository.findById(attempt.getExamId())
                .orElseThrow();

        ExamGrading grading = examGradingRepository
                .findByExamId(exam.getExamId())
                .orElseThrow(() ->
                        new IllegalStateException("Grading not configured"));

        double percentage =
                (attempt.getScore() / exam.getTotalMarks()) * 100;

        boolean passed =
                percentage >= exam.getPassPercentage();

        List<ExamResponse> responses =
                examResponseRepository.findByAttemptId(attemptId);

        long totalQuestions = responses.size();

        long attemptedCount = responses.stream()
                .filter(r -> r.getSelectedOptionId() != null
                        || r.getDescriptiveAnswer() != null
                        || r.getCodingSubmissionPath() != null)
                .count();

        long correctCount = responses.stream()
                .filter(r -> "AUTO".equals(r.getEvaluationType())
                        && r.getMarksAwarded() != null
                        && r.getMarksAwarded() > 0)
                .count();

        long wrongCount = attemptedCount - correctCount;

        Map<String, Object> result = new HashMap<>();
        result.put("attemptId", attemptId);
        result.put("totalQuestions", totalQuestions);
        result.put("attemptedCount", attemptedCount);
        result.put("correctCount", correctCount);
        result.put("wrongCount", wrongCount);
        result.put("percentage", percentage);
        result.put("passed", passed);

        if (grading.getShowResult()) {
            result.put("score", attempt.getScore());
        }

        return result;
    }

    // ================= GET ATTEMPT =================
    @Override
    public ExamAttempt getAttemptById(Long attemptId, Long studentId) {

        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new IllegalStateException("Attempt not found"));

        if (!attempt.getStudentId().equals(studentId)) {
            throw new AccessDeniedException("Attempt does not belong to this student");
        }

        checkAndAutoSubmitIfExpired(attempt);

        return attempt;
    }
}

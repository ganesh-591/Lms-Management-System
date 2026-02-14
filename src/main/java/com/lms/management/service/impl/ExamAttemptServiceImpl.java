package com.lms.management.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.management.model.Exam;
import com.lms.management.model.ExamAttempt;
import com.lms.management.model.ExamQuestion;
import com.lms.management.model.ExamResponse;
import com.lms.management.model.ExamSection;
import com.lms.management.repository.ExamAttemptRepository;
import com.lms.management.repository.ExamGradingRepository;
import com.lms.management.repository.ExamQuestionRepository;
import com.lms.management.repository.ExamRepository;
import com.lms.management.repository.ExamResponseRepository;
import com.lms.management.repository.ExamSectionRepository;
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
    private final ExamSectionRepository examSectionRepository;

    public ExamAttemptServiceImpl(
            ExamAttemptRepository examAttemptRepository,
            ExamRepository examRepository,
            ExamSettingsRepository examSettingsRepository,
            ExamQuestionRepository examQuestionRepository,
            ExamResponseService examResponseService,
            ExamResponseRepository examResponseRepository,
            ExamGradingRepository examGradingRepository,
            CodingExecutionService codingExecutionService,
            ExamSectionRepository examSectionRepository) {

        this.examAttemptRepository = examAttemptRepository;
        this.examRepository = examRepository;
        this.examSettingsRepository = examSettingsRepository;
        this.examQuestionRepository = examQuestionRepository;
        this.examResponseService = examResponseService;
        this.examResponseRepository = examResponseRepository;
        this.examGradingRepository = examGradingRepository;
        this.codingExecutionService = codingExecutionService;
        this.examSectionRepository = examSectionRepository;
    }

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

    @Override
    public ExamAttempt startAttempt(Long examId, Long studentId) {

        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new IllegalStateException("Exam not found"));

        if (!"PUBLISHED".equals(exam.getStatus())) {
            throw new IllegalStateException("Exam not available");
        }

        // ✅ LOAD SECTIONS FIRST
        List<ExamSection> sections =
                examSectionRepository.findByExamIdOrderBySectionOrderAsc(examId);

        if (sections.isEmpty()) {
            throw new IllegalStateException("No sections in exam");
        }

        // ✅ THEN LOAD QUESTIONS FROM EACH SECTION
        List<ExamQuestion> questions = new ArrayList<>();

        for (ExamSection section : sections) {
            questions.addAll(
                    examQuestionRepository
                            .findByExamSectionIdOrderByQuestionOrderAsc(
                                    section.getExamSectionId()
                            )
            );
        }

        if (questions.isEmpty()) {
            throw new IllegalStateException("No questions in exam");
        }

        ExamAttempt attempt = new ExamAttempt();
        attempt.setExamId(examId);
        attempt.setStudentId(studentId);
        attempt.setAttemptNumber(
                examAttemptRepository.countByExamIdAndStudentId(examId, studentId) + 1);
        attempt.setStartTime(LocalDateTime.now());
        attempt.setStatus("IN_PROGRESS");

        attempt = examAttemptRepository.save(attempt);

        for (ExamQuestion q : questions) {
            ExamResponse r = new ExamResponse();
            r.setAttemptId(attempt.getAttemptId());
            r.setExamQuestionId(q.getExamQuestionId());
            r.setMarksAwarded(0.0);
            examResponseRepository.save(r);
        }

        return attempt;
    }

    // 🔥 Everything below remains unchanged
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

        attempt.setStatus("EVALUATING");
        attempt.setEndTime(LocalDateTime.now());

        return examAttemptRepository.save(attempt);
    }

    @Override
    public ExamAttempt autoSubmitAttempt(Long attemptId, Long studentId) {

        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new IllegalStateException("Attempt not found"));

        if (!attempt.getStudentId().equals(studentId)) {
            throw new AccessDeniedException("Not your attempt");
        }

        if (!"IN_PROGRESS".equals(attempt.getStatus())) return attempt;

        attempt.setStatus("EVALUATING");
        attempt.setEndTime(LocalDateTime.now());

        return examAttemptRepository.save(attempt);
    }

    @Override
    public void evaluateAttempt(Long attemptId) {

        ExamAttempt attempt = examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new IllegalStateException("Attempt not found"));

        if (!"EVALUATING".equals(attempt.getStatus())) {
            throw new IllegalStateException("Not ready for evaluation");
        }

        examResponseService.autoEvaluateMcq(attemptId);

        List<ExamResponse> responses =
                examResponseRepository.findByAttemptId(attemptId);

        for (ExamResponse r : responses) {
            if (r.getCodingSubmissionCode() != null
                    && !r.getCodingSubmissionCode().isBlank()) {
                codingExecutionService.runSubmission(r.getResponseId());
            }
        }

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

        double percentage =
                (attempt.getScore() / exam.getTotalMarks()) * 100;

        boolean passed =
                percentage >= exam.getPassPercentage();

        Map<String, Object> result = new HashMap<>();
        result.put("attemptId", attemptId);
        result.put("percentage", percentage);
        result.put("passed", passed);
        result.put("score", attempt.getScore());

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

    @Override
    public ExamAttempt getAttemptByIdForSystem(Long attemptId) {
        return examAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new IllegalStateException("Attempt not found"));
    }

    @Override
    public ExamAttempt updateAttemptStatus(ExamAttempt attempt) {
        return examAttemptRepository.save(attempt);
    }
}
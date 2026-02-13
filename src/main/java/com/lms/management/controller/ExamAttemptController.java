package com.lms.management.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lms.management.model.ExamAttempt;
import com.lms.management.service.EvaluationAsyncService;
import com.lms.management.service.ExamAttemptService;

@RestController
@RequestMapping("/api/exams/{examId}/attempts")
public class ExamAttemptController {

    private final ExamAttemptService examAttemptService;
    private final EvaluationAsyncService evaluationAsyncService;

    public ExamAttemptController(
            ExamAttemptService examAttemptService,
            EvaluationAsyncService evaluationAsyncService) {

        this.examAttemptService = examAttemptService;
        this.evaluationAsyncService = evaluationAsyncService;
    }

    @PostMapping("/start")
    public ResponseEntity<ExamAttempt> startAttempt(
            @PathVariable Long examId,
            Authentication authentication) {

        Long studentId = extractStudentId(authentication);

        return ResponseEntity.ok(
                examAttemptService.startAttempt(examId, studentId)
        );
    }

    @PostMapping("/{attemptId}/submit")
    public ResponseEntity<ExamAttempt> submitAttempt(
            @PathVariable Long attemptId,
            Authentication authentication) {

        Long studentId = extractStudentId(authentication);

        ExamAttempt attempt =
                examAttemptService.submitAttempt(attemptId, studentId);

        evaluationAsyncService.evaluateAttemptAsync(
                attempt.getAttemptId()
        );

        return ResponseEntity.ok(attempt);
    }

    @PostMapping("/{attemptId}/auto-submit")
    public ResponseEntity<ExamAttempt> autoSubmitAttempt(
            @PathVariable Long attemptId,
            Authentication authentication) {

        Long studentId = extractStudentId(authentication);

        ExamAttempt attempt =
                examAttemptService.autoSubmitAttempt(attemptId, studentId);

        evaluationAsyncService.evaluateAttemptAsync(
                attempt.getAttemptId()
        );

        return ResponseEntity.ok(attempt);
    }

    @PostMapping("/{attemptId}/evaluate")
    public ResponseEntity<?> evaluateAttempt(
            @PathVariable Long attemptId) {

        evaluationAsyncService.evaluateAttemptAsync(attemptId);

        return ResponseEntity.ok(
                java.util.Map.of("status", "Evaluation started in background")
        );
    }

    @GetMapping("/{attemptId}")
    public ResponseEntity<ExamAttempt> getAttempt(
            @PathVariable Long attemptId,
            Authentication authentication) {

        Long studentId = extractStudentId(authentication);

        return ResponseEntity.ok(
                examAttemptService.getAttemptById(attemptId, studentId)
        );
    }

    @GetMapping("/{attemptId}/result")
    public ResponseEntity<?> getResult(
            @PathVariable Long attemptId,
            Authentication authentication) {

        Long studentId = extractStudentId(authentication);

        return ResponseEntity.ok(
                examAttemptService.getResult(attemptId, studentId)
        );
    }

    private Long extractStudentId(Authentication authentication) {
        return 1L;
    }
    
    @GetMapping("/{attemptId}/status")
    public ResponseEntity<?> getAttemptStatus(
            @PathVariable Long attemptId,
            Authentication authentication) {

        Long studentId = extractStudentId(authentication);

        ExamAttempt attempt =
                examAttemptService.getAttemptById(attemptId, studentId);

        return ResponseEntity.ok(
                java.util.Map.of("status", attempt.getStatus())
        );
    }
}
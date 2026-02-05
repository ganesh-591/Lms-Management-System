package com.lms.management.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lms.management.model.ExamAttempt;
import com.lms.management.service.ExamAttemptService;

@RestController
@RequestMapping("/api/exams/{examId}/attempts")
public class ExamAttemptController {

    private final ExamAttemptService examAttemptService;

    public ExamAttemptController(ExamAttemptService examAttemptService) {
        this.examAttemptService = examAttemptService;
    }

    // ================= START ATTEMPT =================
    @PostMapping("/start")
    public ResponseEntity<ExamAttempt> startAttempt(
            @PathVariable Long examId,
            Authentication authentication) {

        Long studentId = extractStudentId(authentication);

        return ResponseEntity.ok(
            examAttemptService.startAttempt(examId, studentId)
        );
    }

    // ================= SUBMIT ATTEMPT =================
    @PostMapping("/{attemptId}/submit")
    public ResponseEntity<ExamAttempt> submitAttempt(
            @PathVariable Long attemptId,
            Authentication authentication) {

        Long studentId = extractStudentId(authentication);

        return ResponseEntity.ok(
            examAttemptService.submitAttempt(attemptId, studentId)
        );
    }

    // ================= AUTO SUBMIT =================
    @PostMapping("/{attemptId}/auto-submit")
    public ResponseEntity<ExamAttempt> autoSubmitAttempt(
            @PathVariable Long attemptId,
            Authentication authentication) {

        Long studentId = extractStudentId(authentication);

        return ResponseEntity.ok(
            examAttemptService.autoSubmitAttempt(attemptId, studentId)
        );
    }

    // ================= EVALUATE ATTEMPT =================
    // SYSTEM / ADMIN (JWT role validation later)
    @PostMapping("/{attemptId}/evaluate")
    public ResponseEntity<?> evaluateAttempt(
            @PathVariable Long attemptId) {

        examAttemptService.evaluateAttempt(attemptId);
        return ResponseEntity.ok(
            java.util.Map.of("status", "Evaluation triggered")
        );
    }

    // ================= GET ATTEMPT =================
    @GetMapping("/{attemptId}")
    public ResponseEntity<ExamAttempt> getAttempt(
            @PathVariable Long attemptId,
            Authentication authentication) {

        Long studentId = extractStudentId(authentication);

        return ResponseEntity.ok(
            examAttemptService.getAttemptById(attemptId, studentId)
        );
    }

    // ================= GET RESULT =================
    @GetMapping("/{attemptId}/result")
    public ResponseEntity<?> getResult(
            @PathVariable Long attemptId,
            Authentication authentication) {

        Long studentId = extractStudentId(authentication);

        return ResponseEntity.ok(
            examAttemptService.getResult(attemptId, studentId)
        );
    }

    // ================= TEMP ID EXTRACTION =================
    // This will be replaced when real User table / mapping is added
    private Long extractStudentId(Authentication authentication) {
        // currently JWT username/email → mapped manually
        // example: admin@gmail.com → studentId = 1 (TEMP)
        return 1L;
    }
}

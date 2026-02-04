package com.lms.management.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    // TEST ONLY: studentId passed manually
    @PostMapping("/start")
    public ResponseEntity<ExamAttempt> startAttempt(
            @PathVariable Long examId,
            @RequestParam Long studentId) {

        return ResponseEntity.ok(
            examAttemptService.startAttempt(examId, studentId)
        );
    }


    // ================= SUBMIT ATTEMPT =================
    @PostMapping("/{attemptId}/submit")
    public ResponseEntity<ExamAttempt> submitAttempt(
            @PathVariable Long attemptId,
            @RequestParam Long studentId) {

        return ResponseEntity.ok(
            examAttemptService.submitAttempt(attemptId, studentId)
        );
    }

    // ================= AUTO SUBMIT =================
    // SYSTEM / TEST ONLY
    @PostMapping("/{attemptId}/auto-submit")
    public ResponseEntity<ExamAttempt> autoSubmitAttempt(
            @PathVariable Long attemptId,
            @RequestParam Long studentId) {

        return ResponseEntity.ok(
            examAttemptService.autoSubmitAttempt(attemptId, studentId)
        );
    }

    
 // ================= GET ATTEMPT STATUS =================
    @GetMapping("/{attemptId}")
    public ResponseEntity<ExamAttempt> getAttempt(
            @PathVariable Long attemptId,
            @RequestParam Long studentId) {

        return ResponseEntity.ok(
            examAttemptService.getAttemptById(attemptId, studentId)
        );
    }
}
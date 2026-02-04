package com.lms.management.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.lms.management.model.ExamViolation;
import com.lms.management.service.ExamViolationService;

@RestController
@RequestMapping("/api/exam-attempts/{attemptId}/violations")
public class ExamViolationController {

    private final ExamViolationService examViolationService;

    public ExamViolationController(ExamViolationService examViolationService) {
        this.examViolationService = examViolationService;
    }

    // ================= RECORD VIOLATION =================
    @PostMapping
    @PreAuthorize("hasAuthority('EXAM_VIOLATION_RECORD')")
    public ResponseEntity<ExamViolation> recordViolation(
            @PathVariable Long attemptId,
            @RequestBody Map<String, String> request) {

        return ResponseEntity.ok(
                examViolationService.recordViolation(
                        attemptId,
                        request.get("violationType")
                )
        );
    }

    // ================= GET VIOLATIONS (AUDIT VIEW) =================
    @GetMapping
    @PreAuthorize("hasAuthority('EXAM_VIOLATION_VIEW')")
    public ResponseEntity<List<ExamViolation>> getViolations(
            @PathVariable Long attemptId) {

        return ResponseEntity.ok(
                examViolationService.getViolationsByAttempt(attemptId)
        );
    }

    // ================= GET VIOLATION COUNT (SYSTEM) =================
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getViolationCount(
            @PathVariable Long attemptId) {

        long count = examViolationService.getViolationCount(attemptId);
        return ResponseEntity.ok(
                Map.of("attemptId", attemptId, "violationCount", count)
        );
    }
}

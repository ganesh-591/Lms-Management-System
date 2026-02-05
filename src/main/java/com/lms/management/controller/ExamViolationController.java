package com.lms.management.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            @RequestBody Map<String, String> request,
            Authentication authentication) {

        extractStudentId(authentication);

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
            @PathVariable Long attemptId,
            Authentication authentication) {

        extractViewer(authentication);

        return ResponseEntity.ok(
                examViolationService.getViolationsByAttempt(attemptId)
        );
    }

    // ================= GET VIOLATION COUNT (SYSTEM) =================
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getViolationCount(
            @PathVariable Long attemptId,
            Authentication authentication) {

        extractSystem(authentication);

        long count = examViolationService.getViolationCount(attemptId);
        return ResponseEntity.ok(
                Map.of("attemptId", attemptId, "violationCount", count)
        );
    }

    // ================= TEMP ID EXTRACTION =================
    private Long extractStudentId(Authentication authentication) {
        // TEMP until JWT → student mapping
        return 1L;
    }

    private void extractViewer(Authentication authentication) {
        // TEMP – admin / instructor
    }

    private void extractSystem(Authentication authentication) {
        // TEMP – scheduler / system call
    }
}

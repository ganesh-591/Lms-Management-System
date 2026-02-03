package com.lms.management.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.lms.management.model.ExamGrading;
import com.lms.management.service.ExamGradingService;

@RestController
@RequestMapping("/api/exams/{examId}/grading")
public class ExamGradingController {

    private final ExamGradingService examGradingService;

    public ExamGradingController(ExamGradingService examGradingService) {
        this.examGradingService = examGradingService;
    }

    // Create / update grading rules (DRAFT only)
    @PostMapping
    public ResponseEntity<ExamGrading> saveGrading(
            @PathVariable Long examId,
            @RequestBody ExamGrading grading) {

        return ResponseEntity.ok(
                examGradingService.saveGrading(examId, grading)
        );
    }

    // Get grading rules (read-only)
    @GetMapping
    public ResponseEntity<ExamGrading> getGrading(
            @PathVariable Long examId) {

        return ResponseEntity.ok(
                examGradingService.getGradingByExamId(examId)
        );
    }
}

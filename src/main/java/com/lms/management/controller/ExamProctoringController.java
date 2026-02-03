package com.lms.management.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.lms.management.model.ExamProctoring;
import com.lms.management.service.ExamProctoringService;

@RestController
@RequestMapping("/api/exams/{examId}/proctoring")
public class ExamProctoringController {

    private final ExamProctoringService examProctoringService;

    public ExamProctoringController(ExamProctoringService examProctoringService) {
        this.examProctoringService = examProctoringService;
    }

    // Create / update proctoring rules (DRAFT only)
    @PostMapping
    public ResponseEntity<ExamProctoring> saveProctoring(
            @PathVariable Long examId,
            @RequestBody ExamProctoring proctoring) {

        return ResponseEntity.ok(
                examProctoringService.saveProctoring(examId, proctoring)
        );
    }

    // Get proctoring rules (read-only)
    @GetMapping
    public ResponseEntity<ExamProctoring> getProctoring(
            @PathVariable Long examId) {

        return ResponseEntity.ok(
                examProctoringService.getProctoringByExamId(examId)
        );
    }
}

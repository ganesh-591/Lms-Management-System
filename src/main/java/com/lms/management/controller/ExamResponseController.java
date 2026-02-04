package com.lms.management.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.lms.management.model.ExamResponse;
import com.lms.management.service.ExamResponseService;

@RestController
@RequestMapping("/api/exam-attempts/{attemptId}/responses")
public class ExamResponseController {

    private final ExamResponseService examResponseService;

    public ExamResponseController(ExamResponseService examResponseService) {
        this.examResponseService = examResponseService;
    }

    // ================= SAVE / AUTOSAVE RESPONSE =================
    @PostMapping
    public ResponseEntity<ExamResponse> saveResponse(
            @PathVariable Long attemptId,
            @RequestBody ExamResponse request) {

        return ResponseEntity.ok(
                examResponseService.saveOrUpdateResponse(
                        attemptId,
                        request.getExamQuestionId(),
                        request.getSelectedOptionId(),
                        request.getDescriptiveAnswer(),
                        request.getCodingSubmissionPath()
                )
        );
    }

    // ================= GET RESPONSES (RESUME) =================
    @GetMapping
    @PreAuthorize("hasAuthority('EXAM_RESPONSE_VIEW_SELF')")
    public ResponseEntity<List<ExamResponse>> getResponses(
            @PathVariable Long attemptId) {

        return ResponseEntity.ok(
                examResponseService.getResponsesByAttempt(attemptId)
        );
    }

    // ================= AUTO EVALUATE MCQ =================
    @PostMapping("/auto-evaluate")
    public ResponseEntity<?> autoEvaluateMcq(
            @PathVariable Long attemptId) {

        examResponseService.autoEvaluateMcq(attemptId);
        return ResponseEntity.ok(
                java.util.Map.of("status", "MCQ evaluation completed")
        );
    }
}

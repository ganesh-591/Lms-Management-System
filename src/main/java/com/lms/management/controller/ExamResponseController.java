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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
            @RequestBody ExamResponse request,
            Authentication authentication) {

        extractStudentId(authentication);

        return ResponseEntity.ok(
                examResponseService.saveOrUpdateResponse(
                        attemptId,
                        request.getExamQuestionId(),
                        request.getSelectedOptionId(),
                        request.getDescriptiveAnswer(),
                        request.getCodingSubmissionCode()   // ✅ FIXED
                )
        );
    }

    // ================= GET RESPONSES =================
    @GetMapping
    @PreAuthorize("hasAuthority('EXAM_RESPONSE_VIEW_SELF')")
    public ResponseEntity<List<ExamResponse>> getResponses(
            @PathVariable Long attemptId,
            Authentication authentication) {

        extractStudentId(authentication);

        return ResponseEntity.ok(
                examResponseService.getResponsesByAttempt(attemptId)
        );
    }

    // ================= AUTO EVALUATE MCQ =================
    @PostMapping("/auto-evaluate")
    @PreAuthorize("hasAuthority('EXAM_RESPONSE_EVALUATE')")
    public ResponseEntity<?> autoEvaluateMcq(
            @PathVariable Long attemptId) {

        examResponseService.autoEvaluateMcq(attemptId);

        return ResponseEntity.ok(
                Map.of("status", "MCQ evaluation completed")
        );
    }

    // ================= MANUAL EVALUATION =================
    @PostMapping("/{responseId}/evaluate")
    @PreAuthorize("hasAuthority('EXAM_RESPONSE_EVALUATE')")
    public ResponseEntity<ExamResponse> evaluateResponse(
            @PathVariable Long attemptId,
            @PathVariable Long responseId,
            @RequestParam Double marks,
            Authentication authentication) {

        extractEvaluatorId(authentication);

        return ResponseEntity.ok(
                examResponseService.evaluateResponse(
                        attemptId,
                        responseId,
                        marks
                )
        );
    }

    // ================= CODING RESPONSES (EVALUATOR VIEW) =================
    @GetMapping("/coding-responses")
    @PreAuthorize("hasAuthority('EXAM_RESPONSE_EVALUATE')")
    public ResponseEntity<List<Map<String, Object>>> getCodingResponses(
            @PathVariable Long attemptId) {

        return ResponseEntity.ok(
                examResponseService.getCodingResponsesForEvaluation(attemptId)
        );
    }

    // ================= TEMP ID EXTRACTION =================
    private Long extractStudentId(Authentication authentication) {
        return 1L; // TODO: Replace with JWT extraction
    }

    private Long extractEvaluatorId(Authentication authentication) {
        return 1L; // TODO: Replace with JWT extraction
    }
}

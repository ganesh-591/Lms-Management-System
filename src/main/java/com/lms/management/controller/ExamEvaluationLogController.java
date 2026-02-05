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

import com.lms.management.model.ExamEvaluationLog;
import com.lms.management.service.ExamEvaluationLogService;

@RestController
@RequestMapping("/api/exam-attempts/{attemptId}/evaluation-logs")
public class ExamEvaluationLogController {

    private final ExamEvaluationLogService examEvaluationLogService;

    public ExamEvaluationLogController(
            ExamEvaluationLogService examEvaluationLogService) {
        this.examEvaluationLogService = examEvaluationLogService;
    }

    // ================= CREATE EVALUATION LOG =================
    @PostMapping
    @PreAuthorize("hasAuthority('EXAM_EVALUATION_LOG_CREATE')")
    public ResponseEntity<ExamEvaluationLog> createLog(
            @PathVariable Long attemptId,
            @RequestBody Map<String, Object> request,
            Authentication authentication) {

        Long evaluatorId = extractEvaluatorId(authentication);

        return ResponseEntity.ok(
                examEvaluationLogService.logEvaluationChange(
                        attemptId,
                        evaluatorId,
                        Double.valueOf(request.get("oldScore").toString()),
                        Double.valueOf(request.get("newScore").toString()),
                        request.get("reason").toString()
                )
        );
    }

    // ================= GET EVALUATION LOGS =================
    @GetMapping
    @PreAuthorize("hasAuthority('EXAM_EVALUATION_LOG_VIEW')")
    public ResponseEntity<List<ExamEvaluationLog>> getLogs(
            @PathVariable Long attemptId,
            Authentication authentication) {

        extractViewer(authentication);

        return ResponseEntity.ok(
                examEvaluationLogService.getLogsByAttempt(attemptId)
        );
    }

    // ================= TEMP ID EXTRACTION =================
    private Long extractEvaluatorId(Authentication authentication) {
        // TEMP until JWT → evaluator mapping
        return 99L; // admin / instructor placeholder
    }

    private void extractViewer(Authentication authentication) {
        // TEMP – admin / instructor
    }
}

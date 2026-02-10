package com.lms.management.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lms.management.model.CodingExecutionResult;
import com.lms.management.service.CodingExecutionService;

@RestController
@RequestMapping("/api/exam-responses/{responseId}")
public class CodingExecutionController {

    private final CodingExecutionService codingExecutionService;

    public CodingExecutionController(
            CodingExecutionService codingExecutionService) {
        this.codingExecutionService = codingExecutionService;
    }

    // RUN CODE (PHASE 1)
    @PostMapping("/run")
    @PreAuthorize("hasAuthority('EXAM_RESPONSE_EVALUATE')")
    public ResponseEntity<?> run(@PathVariable Long responseId) {

        codingExecutionService.runSubmission(responseId);
        return ResponseEntity.ok(
                java.util.Map.of("status", "Execution completed")
        );
    }

    // VIEW RESULTS
    @GetMapping("/execution-results")
    @PreAuthorize("hasAuthority('EXAM_RESPONSE_EVALUATE')")
    public ResponseEntity<List<CodingExecutionResult>> getResults(
            @PathVariable Long responseId) {

        return ResponseEntity.ok(
                codingExecutionService.getResultsByResponse(responseId)
        );
    }
}

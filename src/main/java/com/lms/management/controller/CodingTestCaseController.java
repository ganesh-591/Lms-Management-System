package com.lms.management.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lms.management.model.CodingTestCase;
import com.lms.management.service.CodingTestCaseService;

@RestController
@RequestMapping("/api/questions/{questionId}/coding-test-cases")
public class CodingTestCaseController {

    private final CodingTestCaseService codingTestCaseService;

    public CodingTestCaseController(CodingTestCaseService codingTestCaseService) {
        this.codingTestCaseService = codingTestCaseService;
    }

    // ================= CREATE MULTIPLE TEST CASES =================
    // POST /api/questions/{questionId}/coding-test-cases
    @PostMapping
    @PreAuthorize("hasAuthority('CODING_TEST_CASE_MANAGE')")
    public ResponseEntity<List<CodingTestCase>> createTestCases(
            @PathVariable Long questionId,
            @RequestBody List<CodingTestCase> requests) {

        return ResponseEntity.ok(
                codingTestCaseService.createMultipleTestCases(
                        questionId,
                        requests
                )
        );
    }

    // ================= GET TEST CASES =================
    // GET /api/questions/{questionId}/coding-test-cases
    @GetMapping
    @PreAuthorize("hasAuthority('CODING_TEST_CASE_VIEW')")
    public ResponseEntity<List<CodingTestCase>> getTestCases(
            @PathVariable Long questionId) {

        return ResponseEntity.ok(
                codingTestCaseService.getTestCasesByQuestion(questionId)
        );
    }

    // ================= UPDATE TEST CASE =================
    // PUT /api/questions/{questionId}/coding-test-cases/{testCaseId}
    @PutMapping("/{testCaseId}")
    @PreAuthorize("hasAuthority('CODING_TEST_CASE_MANAGE')")
    public ResponseEntity<CodingTestCase> updateTestCase(
            @PathVariable Long questionId,
            @PathVariable Long testCaseId,
            @RequestBody CodingTestCase request) {

        return ResponseEntity.ok(
                codingTestCaseService.updateTestCase(
                        testCaseId,
                        request.getInputData(),
                        request.getExpectedOutput(),
                        request.getHidden()
                )
        );
    }

    // ================= DELETE TEST CASE =================
    // DELETE /api/questions/{questionId}/coding-test-cases/{testCaseId}
    @DeleteMapping("/{testCaseId}")
    @PreAuthorize("hasAuthority('CODING_TEST_CASE_MANAGE')")
    public ResponseEntity<Void> deleteTestCase(
            @PathVariable Long questionId,
            @PathVariable Long testCaseId) {

        codingTestCaseService.deleteTestCase(testCaseId);
        return ResponseEntity.noContent().build();
    }
}
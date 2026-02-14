package com.lms.management.service;

import java.util.List;

import com.lms.management.model.CodingTestCase;

public interface CodingTestCaseService {

    // 🔥 NEW — create multiple test cases at once
    List<CodingTestCase> createMultipleTestCases(
            Long questionId,
            List<CodingTestCase> testCases
    );

    CodingTestCase updateTestCase(
            Long testCaseId,
            String inputData,
            String expectedOutput,
            Boolean hidden
    );

    List<CodingTestCase> getTestCasesByQuestion(Long questionId);

    void deleteTestCase(Long testCaseId);
}
package com.lms.management.service;

import java.util.List;

import com.lms.management.model.CodingTestCase;

public interface CodingTestCaseService {

    CodingTestCase createTestCase(
            Long questionId,
            String inputData,
            String expectedOutput,
            Boolean hidden
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

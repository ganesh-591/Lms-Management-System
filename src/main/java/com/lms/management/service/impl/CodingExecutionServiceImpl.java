package com.lms.management.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.management.model.CodingExecutionResult;
import com.lms.management.model.CodingTestCase;
import com.lms.management.model.ExamResponse;
import com.lms.management.repository.CodingExecutionResultRepository;
import com.lms.management.repository.CodingTestCaseRepository;
import com.lms.management.repository.ExamResponseRepository;
import com.lms.management.service.CodingExecutionService;

@Service
@Transactional
public class CodingExecutionServiceImpl implements CodingExecutionService {

    private final ExamResponseRepository examResponseRepository;
    private final CodingTestCaseRepository codingTestCaseRepository;
    private final CodingExecutionResultRepository executionResultRepository;

    public CodingExecutionServiceImpl(
            ExamResponseRepository examResponseRepository,
            CodingTestCaseRepository codingTestCaseRepository,
            CodingExecutionResultRepository executionResultRepository) {

        this.examResponseRepository = examResponseRepository;
        this.codingTestCaseRepository = codingTestCaseRepository;
        this.executionResultRepository = executionResultRepository;
    }

    @Override
    public void runSubmission(Long responseId) {

        ExamResponse response = examResponseRepository.findById(responseId)
                .orElseThrow(() -> new IllegalStateException("Response not found"));

        if (response.getCodingSubmissionPath() == null
                || response.getCodingSubmissionPath().isBlank()) {
            throw new IllegalStateException("No code submitted");
        }

        List<CodingTestCase> testCases =
                codingTestCaseRepository.findByQuestionId(
                        response.getExamQuestionId()
                );

        if (testCases.isEmpty()) {
            throw new IllegalStateException("No test cases configured");
        }

        // Clear old results
        executionResultRepository.findByResponseId(responseId)
                .forEach(executionResultRepository::delete);

        for (CodingTestCase testCase : testCases) {

            long startTime = System.currentTimeMillis();

            String actualOutput;
            boolean passed;
            String errorMessage = null;

            try {
                // 🔒 PHASE 2: simulated execution
                actualOutput =
                        response.getCodingSubmissionPath()
                                .contains(testCase.getExpectedOutput())
                                ? testCase.getExpectedOutput()
                                : "WRONG_OUTPUT";

                passed = actualOutput.trim()
                        .equals(testCase.getExpectedOutput().trim());

            } catch (Exception e) {
                actualOutput = null;
                passed = false;
                errorMessage = e.getMessage();
            }

            long executionTime =
                    System.currentTimeMillis() - startTime;

            CodingExecutionResult result = new CodingExecutionResult();
            result.setResponseId(responseId);
            result.setTestCaseId(testCase.getTestCaseId());
            result.setActualOutput(actualOutput);
            result.setPassed(passed);
            result.setExecutionTimeMs(executionTime);
            result.setErrorMessage(errorMessage);

            executionResultRepository.save(result);
        }
    }
    
    @Override
    public List<CodingExecutionResult> getResultsByResponse(Long responseId) {
        return executionResultRepository.findByResponseId(responseId);
    }
}

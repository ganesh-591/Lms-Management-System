package com.lms.management.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.management.model.CodingTestCase;
import com.lms.management.model.Question;
import com.lms.management.repository.CodingTestCaseRepository;
import com.lms.management.repository.QuestionRepository;
import com.lms.management.service.CodingTestCaseService;

@Service
@Transactional
public class CodingTestCaseServiceImpl implements CodingTestCaseService {

    private final CodingTestCaseRepository codingTestCaseRepository;
    private final QuestionRepository questionRepository;

    public CodingTestCaseServiceImpl(
            CodingTestCaseRepository codingTestCaseRepository,
            QuestionRepository questionRepository) {
        this.codingTestCaseRepository = codingTestCaseRepository;
        this.questionRepository = questionRepository;
    }

    // 🔥 CREATE MULTIPLE TEST CASES AT ONCE
    @Override
    public List<CodingTestCase> createMultipleTestCases(
            Long questionId,
            List<CodingTestCase> testCases) {

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() ->
                        new IllegalStateException("Question not found"));

        if (!"CODING".equals(question.getQuestionType())) {
            throw new IllegalStateException(
                    "Test cases allowed only for CODING questions");
        }

        List<CodingTestCase> savedCases = new ArrayList<>();

        for (CodingTestCase request : testCases) {

            CodingTestCase testCase = new CodingTestCase();
            testCase.setQuestionId(questionId);
            testCase.setInputData(request.getInputData());
            testCase.setExpectedOutput(request.getExpectedOutput());
            testCase.setHidden(
                    request.getHidden() != null ? request.getHidden() : false
            );

            savedCases.add(
                    codingTestCaseRepository.save(testCase)
            );
        }

        return savedCases;
    }

    @Override
    public CodingTestCase updateTestCase(
            Long testCaseId,
            String inputData,
            String expectedOutput,
            Boolean hidden) {

        CodingTestCase testCase =
                codingTestCaseRepository.findById(testCaseId)
                        .orElseThrow(() ->
                                new IllegalStateException("Test case not found"));

        testCase.setInputData(inputData);
        testCase.setExpectedOutput(expectedOutput);

        if (hidden != null) {
            testCase.setHidden(hidden);
        }

        return codingTestCaseRepository.save(testCase);
    }

    @Override
    public List<CodingTestCase> getTestCasesByQuestion(Long questionId) {
        return codingTestCaseRepository.findByQuestionId(questionId);
    }

    @Override
    public void deleteTestCase(Long testCaseId) {
        codingTestCaseRepository.deleteById(testCaseId);
    }
}
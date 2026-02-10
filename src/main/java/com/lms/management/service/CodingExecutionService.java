package com.lms.management.service;

import java.util.List;

import com.lms.management.model.CodingExecutionResult;

public interface CodingExecutionService {

    void runSubmission(Long responseId);

    List<CodingExecutionResult> getResultsByResponse(Long responseId);
}

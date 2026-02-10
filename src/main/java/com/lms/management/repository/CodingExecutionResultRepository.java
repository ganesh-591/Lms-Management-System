package com.lms.management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lms.management.model.CodingExecutionResult;

public interface CodingExecutionResultRepository
        extends JpaRepository<CodingExecutionResult, Long> {

    List<CodingExecutionResult> findByResponseId(Long responseId);
}

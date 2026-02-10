package com.lms.management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lms.management.model.CodingTestCase;

public interface CodingTestCaseRepository
        extends JpaRepository<CodingTestCase, Long> {

    List<CodingTestCase> findByQuestionId(Long questionId);
}

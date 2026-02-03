package com.lms.management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.management.model.ExamViolation;

@Repository
public interface ExamViolationRepository
        extends JpaRepository<ExamViolation, Long> {

    List<ExamViolation> findByAttemptId(Long attemptId);

    long countByAttemptId(Long attemptId);
}

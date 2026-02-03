package com.lms.management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.management.model.ExamEvaluationLog;

@Repository
public interface ExamEvaluationLogRepository
        extends JpaRepository<ExamEvaluationLog, Long> {

    List<ExamEvaluationLog> findByAttemptId(Long attemptId);
}

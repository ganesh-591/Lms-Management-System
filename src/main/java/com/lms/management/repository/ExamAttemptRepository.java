package com.lms.management.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.management.model.ExamAttempt;

@Repository
public interface ExamAttemptRepository
        extends JpaRepository<ExamAttempt, Long> {

    boolean existsByExamIdAndStudentIdAndStatus(
            Long examId, Long studentId, String status);

    int countByExamIdAndStudentId(Long examId, Long studentId);

    Optional<ExamAttempt> findFirstByExamIdAndStudentIdAndStatus(
            Long examId, Long studentId, String status);
}

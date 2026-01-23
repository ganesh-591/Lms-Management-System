package com.lms.management.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lms.management.model.StudentBatch;

public interface StudentBatchRepository
        extends JpaRepository<StudentBatch, Long> {

    // View students in a batch
    List<StudentBatch> findByBatchId(Long batchId);

    // View student's current active batch
    Optional<StudentBatch> findFirstByStudentIdAndStatus(
            Long studentId,
            String status
    );

    // 🔒 Validation checks
    boolean existsByStudentIdAndBatchIdAndStatus(
            Long studentId,
            Long batchId,
            String status
    );

    boolean existsByStudentIdAndCourseIdAndStatus(
            Long studentId,
            Long courseId,
            String status
    );
}

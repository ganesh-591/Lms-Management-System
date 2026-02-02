package com.lms.management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.management.model.Exam;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {

    // Get all exams under a course
    List<Exam> findByCourseId(Long courseId);

    // Get all exams under a batch
    List<Exam> findByBatchId(Long batchId);

    // Get exams by status (DRAFT / PUBLISHED / CLOSED)
    List<Exam> findByStatus(String status);

    // Check if a published exam exists for a batch
    boolean existsByBatchIdAndStatus(Long batchId, String status);
}

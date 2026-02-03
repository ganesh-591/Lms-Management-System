package com.lms.management.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.management.model.Exam;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {

    // Get single exam (not deleted)
    Optional<Exam> findByExamIdAndIsDeletedFalse(Long examId);

    // Get exams by course (not deleted)
    List<Exam> findByCourseIdAndIsDeletedFalse(Long courseId);

    // Get exams by batch (not deleted)
    List<Exam> findByBatchIdAndIsDeletedFalse(Long batchId);
}

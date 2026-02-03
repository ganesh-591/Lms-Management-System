package com.lms.management.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.management.model.ExamProctoring;

@Repository
public interface ExamProctoringRepository extends JpaRepository<ExamProctoring, Long> {

    Optional<ExamProctoring> findByExamId(Long examId);

    boolean existsByExamId(Long examId);
}

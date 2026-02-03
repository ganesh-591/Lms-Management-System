package com.lms.management.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.management.model.ExamDesign;

@Repository
public interface ExamDesignRepository extends JpaRepository<ExamDesign, Long> {

    Optional<ExamDesign> findByExamId(Long examId);

    boolean existsByExamId(Long examId);
}

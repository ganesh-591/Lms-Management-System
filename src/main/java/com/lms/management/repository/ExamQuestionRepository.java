package com.lms.management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.management.model.ExamQuestion;

@Repository
public interface ExamQuestionRepository
        extends JpaRepository<ExamQuestion, Long> {

    List<ExamQuestion> findByExamSectionIdOrderByQuestionOrderAsc(Long examSectionId);

    // 🔥 Prevent duplicate question inside same section
    boolean existsByExamSectionIdAndQuestionId(Long examSectionId, Long questionId);
}
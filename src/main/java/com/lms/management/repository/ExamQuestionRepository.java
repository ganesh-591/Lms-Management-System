package com.lms.management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.management.model.ExamQuestion;

@Repository
public interface ExamQuestionRepository
        extends JpaRepository<ExamQuestion, Long> {

    List<ExamQuestion> findByExamIdOrderByQuestionOrderAsc(Long examId);

    boolean existsByExamIdAndQuestionId(Long examId, Long questionId);
}

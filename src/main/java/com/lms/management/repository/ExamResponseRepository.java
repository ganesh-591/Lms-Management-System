package com.lms.management.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.management.model.ExamResponse;

@Repository
public interface ExamResponseRepository
        extends JpaRepository<ExamResponse, Long> {

    Optional<ExamResponse> findByAttemptIdAndExamQuestionId(
            Long attemptId, Long examQuestionId);

    List<ExamResponse> findByAttemptId(Long attemptId);
}

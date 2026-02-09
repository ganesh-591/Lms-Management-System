package com.lms.management.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.management.model.QuestionDescriptiveAnswer;

@Repository
public interface QuestionDescriptiveAnswerRepository
        extends JpaRepository<QuestionDescriptiveAnswer, Long> {

    // One descriptive model answer per question
    Optional<QuestionDescriptiveAnswer> findByQuestionId(Long questionId);

    boolean existsByQuestionId(Long questionId);
}

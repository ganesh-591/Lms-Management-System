package com.lms.management.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.lms.management.model.Question;
import com.lms.management.model.SectionQuestion;

@Repository
public interface SectionQuestionRepository
        extends JpaRepository<SectionQuestion, Long> {

    // Find mapping by section + question
    Optional<SectionQuestion> findBySectionIdAndQuestionId(
            Long sectionId,
            Long questionId
    );

    // Get all questions inside a section
    @Query("""
            SELECT q
            FROM Question q
            JOIN SectionQuestion sq
                ON q.questionId = sq.questionId
            WHERE sq.sectionId = :sectionId
            """)
    List<Question> findQuestionsBySectionId(Long sectionId);
}
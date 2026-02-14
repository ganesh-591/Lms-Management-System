package com.lms.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.management.model.QuestionSection;

@Repository
public interface QuestionSectionRepository
        extends JpaRepository<QuestionSection, Long> {

}
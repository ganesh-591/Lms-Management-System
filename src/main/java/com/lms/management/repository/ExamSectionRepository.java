package com.lms.management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.management.model.ExamSection;

@Repository
public interface ExamSectionRepository
        extends JpaRepository<ExamSection, Long> {

    List<ExamSection> findByExamIdOrderBySectionOrderAsc(Long examId);
}
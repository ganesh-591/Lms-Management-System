package com.lms.management.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.management.model.ExamSettings;

@Repository
public interface ExamSettingsRepository extends JpaRepository<ExamSettings, Long> {

    Optional<ExamSettings> findByExamId(Long examId);

    boolean existsByExamId(Long examId);
}

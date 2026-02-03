package com.lms.management.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.management.model.ExamNotification;

@Repository
public interface ExamNotificationRepository
        extends JpaRepository<ExamNotification, Long> {

    Optional<ExamNotification> findByExamId(Long examId);
}

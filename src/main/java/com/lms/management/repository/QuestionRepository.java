package com.lms.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.management.model.Question; // 🔴 THIS LINE IS CRITICAL

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
}

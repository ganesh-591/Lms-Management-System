package com.lms.management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lms.management.model.Topic;

public interface TopicRepository extends JpaRepository<Topic, Long> {

    // Get all topics under a specific course
    List<Topic> findByCourseCourseId(Long courseId);

    // Get topics by course and status (optional use)
    List<Topic> findByCourseCourseIdAndStatus(Long courseId, String status);
}

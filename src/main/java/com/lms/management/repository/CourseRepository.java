package com.lms.management.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lms.management.model.Course;

public interface CourseRepository extends JpaRepository<Course, Long> {

    Optional<Course> findByShareCode(String shareCode);
}

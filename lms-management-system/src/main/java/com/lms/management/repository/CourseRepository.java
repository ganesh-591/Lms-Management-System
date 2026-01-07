package com.lms.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lms.management.model.Course;

public interface CourseRepository extends JpaRepository<Course, Long> {

    boolean existsByCourseName(String courseName);
}

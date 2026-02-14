package com.lms.management.service;

import java.util.List;

import com.lms.management.model.Course;

public interface CourseService {

    Course createCourse(Course course);

    List<Course> getAllCourses();

    Course getCourseById(Long courseId);

    Course updateCourse(Long courseId, Course course);

    void deleteCourse(Long courseId);

    void hardDeleteCourse(Long courseId);
}
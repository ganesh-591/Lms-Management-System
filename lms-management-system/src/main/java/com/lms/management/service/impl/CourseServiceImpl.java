package com.lms.management.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.lms.management.exception.ResourceNotFoundException;
import com.lms.management.model.Course;
import com.lms.management.repository.CourseRepository;
import com.lms.management.service.CourseService;

@Service
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;

    public CourseServiceImpl(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Override
    public Course createCourse(Course course) {
        applyValidityRule(course);
        return courseRepository.save(course);
    }

    @Override
    public Course updateCourse(Long courseId, Course incoming) {

        Course existing = courseRepository.findById(courseId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Course not found with id: " + courseId)
                );

        // ===== PATCH-LIKE MERGE =====
        if (incoming.getCourseName() != null)
            existing.setCourseName(incoming.getCourseName());

        if (incoming.getDescription() != null)
            existing.setDescription(incoming.getDescription());

        if (incoming.getDuration() != null)
            existing.setDuration(incoming.getDuration());

        if (incoming.getToolsCovered() != null)
            existing.setToolsCovered(incoming.getToolsCovered());

        if (incoming.getCourseFee() != null)
            existing.setCourseFee(incoming.getCourseFee());

        if (incoming.getCertificateProvided() != null)
            existing.setCertificateProvided(incoming.getCertificateProvided());

        if (incoming.getStatus() != null)
            existing.setStatus(incoming.getStatus());

        // ✅ FIX: COURSE IMAGE URL
        if (incoming.getCourseImageUrl() != null)
            existing.setCourseImageUrl(incoming.getCourseImageUrl());

        if (incoming.getShowValidity() != null)
            existing.setShowValidity(incoming.getShowValidity());

        if (incoming.getValidityInDays() != null)
            existing.setValidityInDays(incoming.getValidityInDays());

        if (incoming.getAllowOfflineMobile() != null)
            existing.setAllowOfflineMobile(incoming.getAllowOfflineMobile());

        if (incoming.getAllowBookmark() != null)
            existing.setAllowBookmark(incoming.getAllowBookmark());

        if (incoming.getEnableContentAccess() != null)
            existing.setEnableContentAccess(incoming.getEnableContentAccess());

        // ===== BUSINESS RULE =====
        applyValidityRule(existing);

        return courseRepository.save(existing);
    }

    @Override
    public Course getCourseById(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Course not found with id: " + courseId)
                );
    }

    @Override
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    @Override
    public void deleteCourse(Long courseId) {
        Course course = getCourseById(courseId);
        courseRepository.delete(course);
    }

    // ===== VALIDITY RULE =====
    private void applyValidityRule(Course course) {
        if (Boolean.FALSE.equals(course.getShowValidity())) {
            course.setValidityInDays(null);
        }
    }
}

package com.lms.management.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;

import com.lms.management.exception.ResourceNotFoundException;
import com.lms.management.exception.UnauthorizedAccessException;
import com.lms.management.model.Course;
import com.lms.management.model.Topic;
import com.lms.management.model.TopicContent;
import com.lms.management.repository.CourseRepository;
import com.lms.management.repository.TopicContentRepository;
import com.lms.management.repository.TopicRepository;
import com.lms.management.service.CourseService;
import com.lms.management.util.FileUploadUtil;

@RestController
@RequestMapping("/api/courses")
@CrossOrigin
public class CourseController {

    private final CourseService courseService;
    private final CourseRepository courseRepository;
    private final TopicRepository topicRepository;
    private final TopicContentRepository topicContentRepository;

    public CourseController(
            CourseService courseService,
            CourseRepository courseRepository,
            TopicRepository topicRepository,
            TopicContentRepository topicContentRepository
    ) {
        this.courseService = courseService;
        this.courseRepository = courseRepository;
        this.topicRepository = topicRepository;
        this.topicContentRepository = topicContentRepository;
    }

    // ===============================
    // CREATE COURSE (JSON ONLY)
    // ===============================
    @PostMapping
    public Course createCourse(@RequestBody Course course) {
        return courseService.createCourse(course);
    }

    // ===============================
    // UPDATE COURSE (PUT AS PATCH)
    // ===============================
    @PutMapping("/{id}")
    public Course updateCourse(
            @PathVariable Long id,
            @RequestBody Course course) {

        return courseService.updateCourse(id, course);
    }

    // ===============================
    // UPLOAD / UPDATE COURSE IMAGE
    // ===============================
    @PutMapping("/{id}/image")
    public Course updateCourseImage(
            @PathVariable Long id,
            @RequestParam("image") MultipartFile image
    ) throws IOException {

        String imageUrl = FileUploadUtil.saveCourseImage(image);

        Course incoming = new Course();
        incoming.setCourseImageUrl(imageUrl);

        return courseService.updateCourse(id, incoming);
    }

    // ===============================
    // GET ALL COURSES
    // ===============================
    @GetMapping
    public List<Course> getAllCourses() {
        return courseService.getAllCourses();
    }

    // ===============================
    // GET COURSE BY ID
    // ===============================
    @GetMapping("/{id}")
    public Course getCourse(@PathVariable Long id) {
        return courseService.getCourseById(id);
    }

    // ===============================
    // DELETE COURSE (SOFT DELETE)
    // ===============================
    @DeleteMapping("/{id}")
    public void deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
    }

    // ===============================
    // DELETE COURSE (HARD DELETE)
    // ===============================
    @DeleteMapping("/{id}/hard")
    public void hardDeleteCourse(@PathVariable Long id) {
        courseService.hardDeleteCourse(id);
    }

    // ===============================
    // 🔗 SHARE COURSE (PUBLIC API)
    // RETURNS COURSE + TOPICS + CONTENTS
    // ===============================
    @GetMapping("/share/{shareCode}")
    public Course shareCourse(@PathVariable String shareCode) {

        Course course = courseRepository.findByShareCode(shareCode)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Invalid share link")
                );

        if (!"ACTIVE".equalsIgnoreCase(course.getStatus())) {
            throw new UnauthorizedAccessException("Course is not active");
        }

        if (Boolean.FALSE.equals(course.getShareEnabled())) {
            throw new UnauthorizedAccessException("Sharing disabled");
        }

        if (Boolean.FALSE.equals(course.getEnableContentAccess())) {
            throw new UnauthorizedAccessException("Content access disabled");
        }

        // 🔥 LOAD TOPICS
        List<Topic> topics =
                topicRepository.findByCourseCourseId(course.getCourseId());

        // 🔥 LOAD CONTENTS FOR EACH TOPIC
        for (Topic topic : topics) {
            List<TopicContent> contents =
                    topicContentRepository.findByTopicTopicId(topic.getTopicId());
            topic.setContents(contents);
        }

        // 🔥 ATTACH TO COURSE (TRANSIENT FIELD)
        course.setTopics(topics);

        return course;
    }
}

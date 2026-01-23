package com.lms.management.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lms.management.exception.ResourceNotFoundException;
import com.lms.management.exception.UnauthorizedAccessException;
import com.lms.management.model.Course;
import com.lms.management.model.Topic;
import com.lms.management.repository.CourseRepository;
import com.lms.management.service.TopicService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/topics")
@RequiredArgsConstructor
public class TopicController {

    private final TopicService topicService;
    private final CourseRepository courseRepository;

    // ===============================
    // CREATE TOPIC (NO ACCESS CHECK)
    // ===============================
    @PostMapping("/course/{courseId}")
    public ResponseEntity<Topic> createTopic(
            @PathVariable Long courseId,
            @RequestBody Topic topic) {

        Topic createdTopic = topicService.createTopic(courseId, topic);
        return new ResponseEntity<>(createdTopic, HttpStatus.CREATED);
    }

    // ===============================
    // GET ALL TOPICS (ADMIN USE)
    // ===============================
    @GetMapping
    public ResponseEntity<List<Topic>> getAllTopics() {

        List<Topic> topics = topicService.getAllTopics();
        return ResponseEntity.ok(topics);
    }

    // ===============================
    // GET TOPIC BY ID (NO COURSE CHECK)
    // ===============================
    @GetMapping("/{topicId}")
    public ResponseEntity<Topic> getTopicById(@PathVariable Long topicId) {

        Topic topic = topicService.getTopicById(topicId);
        return ResponseEntity.ok(topic);
    }

    // ===============================
    // GET TOPICS BY COURSE (ACCESS ENFORCED)
    // ===============================
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<Topic>> getTopicsByCourseId(
            @PathVariable Long courseId) {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Course not found with id: " + courseId)
                );

        // 🔒 ENFORCE CONTENT ACCESS
        if (Boolean.FALSE.equals(course.getEnableContentAccess())) {
            throw new UnauthorizedAccessException("Course content access is disabled");
        }

        List<Topic> topics = topicService.getTopicsByCourseId(courseId);
        return ResponseEntity.ok(topics);
    }

    // ===============================
    // UPDATE TOPIC (NO ACCESS CHECK)
    // ===============================
    @PutMapping("/{topicId}")
    public ResponseEntity<Topic> updateTopic(
            @PathVariable Long topicId,
            @RequestBody Topic topic) {

        Topic updatedTopic = topicService.updateTopic(topicId, topic);
        return ResponseEntity.ok(updatedTopic);
    }

    // ===============================
    // DELETE TOPIC (NO ACCESS CHECK)
    // ===============================
    @DeleteMapping("/{topicId}")
    public ResponseEntity<Void> deleteTopic(@PathVariable Long topicId) {

        topicService.deleteTopic(topicId);
        return ResponseEntity.noContent().build();
    }
}

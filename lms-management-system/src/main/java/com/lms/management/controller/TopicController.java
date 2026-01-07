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

import com.lms.management.model.Topic;
import com.lms.management.service.TopicService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/topics")
@RequiredArgsConstructor
public class TopicController {

    private final TopicService topicService;

    // Create topic under a course
    @PostMapping("/course/{courseId}")
    public ResponseEntity<Topic> createTopic(
            @PathVariable Long courseId,
            @RequestBody Topic topic) {

        Topic createdTopic = topicService.createTopic(courseId, topic);
        return new ResponseEntity<>(createdTopic, HttpStatus.CREATED);
    }

    // ✅ NEW: Get all topics (admin use)
    @GetMapping
    public ResponseEntity<List<Topic>> getAllTopics() {

        List<Topic> topics = topicService.getAllTopics();
        return ResponseEntity.ok(topics);
    }

    // Get topic by ID
    @GetMapping("/{topicId}")
    public ResponseEntity<Topic> getTopicById(@PathVariable Long topicId) {

        Topic topic = topicService.getTopicById(topicId);
        return ResponseEntity.ok(topic);
    }

    // Get all topics by course ID
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<Topic>> getTopicsByCourseId(
            @PathVariable Long courseId) {

        List<Topic> topics = topicService.getTopicsByCourseId(courseId);
        return ResponseEntity.ok(topics);
    }

    // Update topic (PUT behaves like PATCH)
    @PutMapping("/{topicId}")
    public ResponseEntity<Topic> updateTopic(
            @PathVariable Long topicId,
            @RequestBody Topic topic) {

        Topic updatedTopic = topicService.updateTopic(topicId, topic);
        return ResponseEntity.ok(updatedTopic);
    }

    // Delete topic
    @DeleteMapping("/{topicId}")
    public ResponseEntity<Void> deleteTopic(@PathVariable Long topicId) {

        topicService.deleteTopic(topicId);
        return ResponseEntity.noContent().build();
    }
}

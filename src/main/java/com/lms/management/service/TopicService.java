package com.lms.management.service;

import java.util.List;

import com.lms.management.model.Topic;

public interface TopicService {

    // Create a new topic under a course
    Topic createTopic(Long courseId, Topic topic);

    // Get a topic by its ID
    Topic getTopicById(Long topicId);

    // Get all topics of a specific course
    List<Topic> getTopicsByCourseId(Long courseId);

    // ✅ NEW: Get all topics (admin use)
    List<Topic> getAllTopics();

    // Update topic (PUT behaves like PATCH)
    Topic updateTopic(Long topicId, Topic topic);

    // Delete a topic
    void deleteTopic(Long topicId);
}

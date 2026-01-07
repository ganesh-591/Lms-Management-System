package com.lms.management.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.lms.management.exception.ResourceNotFoundException;
import com.lms.management.model.Course;
import com.lms.management.model.Topic;
import com.lms.management.repository.CourseRepository;
import com.lms.management.repository.TopicRepository;
import com.lms.management.service.TopicService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TopicServiceImpl implements TopicService {

    private final TopicRepository topicRepository;
    private final CourseRepository courseRepository;

    @Override
    public Topic createTopic(Long courseId, Topic topic) {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        topic.setCourse(course);
        return topicRepository.save(topic);
    }

    @Override
    public Topic getTopicById(Long topicId) {
        return topicRepository.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found"));
    }

    @Override
    public List<Topic> getTopicsByCourseId(Long courseId) {
        return topicRepository.findByCourseCourseId(courseId);
    }

    // ✅ NEW: Get all topics
    @Override
    public List<Topic> getAllTopics() {
        return topicRepository.findAll();
    }

    @Override
    public Topic updateTopic(Long topicId, Topic request) {

        Topic existing = topicRepository.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found"));

        // PUT behaves like PATCH
        if (request.getTopicName() != null)
            existing.setTopicName(request.getTopicName());

        if (request.getTopicDescription() != null)
            existing.setTopicDescription(request.getTopicDescription());

        if (request.getStatus() != null)
            existing.setStatus(request.getStatus());

        return topicRepository.save(existing);
    }

    @Override
    public void deleteTopic(Long topicId) {

        Topic existing = topicRepository.findById(topicId)
                .orElseThrow(() -> new ResourceNotFoundException("Topic not found"));

        topicRepository.delete(existing);
    }
}

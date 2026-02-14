package com.lms.management.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                .orElseThrow(() ->
                        new ResourceNotFoundException("Course not found with id: " + courseId)
                );

        topic.setCourse(course);
        return topicRepository.save(topic);
    }

    @Override
    @Transactional(readOnly = true)
    public Topic getTopicById(Long topicId) {

        return topicRepository.findById(topicId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Topic not found with id: " + topicId)
                );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Topic> getTopicsByCourseId(Long courseId) {

        courseRepository.findById(courseId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Course not found with id: " + courseId)
                );

        return topicRepository.findByCourseCourseId(courseId);
    }

    @Override
    public List<Topic> getAllTopics() {
        return topicRepository.findAll();
    }

    @Override
    public Topic updateTopic(Long topicId, Topic request) {

        Topic existing = topicRepository.findById(topicId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Topic not found with id: " + topicId)
                );

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
                .orElseThrow(() ->
                        new ResourceNotFoundException("Topic not found with id: " + topicId)
                );

        topicRepository.delete(existing);
    }
}
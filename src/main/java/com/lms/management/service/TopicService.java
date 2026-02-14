package com.lms.management.service;

import java.util.List;

import com.lms.management.model.Topic;

public interface TopicService {

    Topic createTopic(Long courseId, Topic topic);

    Topic getTopicById(Long topicId);

    List<Topic> getTopicsByCourseId(Long courseId);

    List<Topic> getAllTopics();

    Topic updateTopic(Long topicId, Topic topic);

    void deleteTopic(Long topicId);
}
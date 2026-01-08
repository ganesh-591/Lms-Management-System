package com.lms.management.service;

import java.util.List;

import com.lms.management.model.TopicContent;

public interface TopicContentService {

    TopicContent createContent(Long topicId, TopicContent content);

    List<TopicContent> createContentBulk(Long topicId, List<TopicContent> contents);

    // ✅ ADD THIS
    List<TopicContent> getAllContents();

    TopicContent getContentById(Long contentId);

    List<TopicContent> getContentsByTopicId(Long topicId);

    TopicContent updateContent(Long contentId, TopicContent incoming);

    void deleteContent(Long contentId);
}

package com.lms.management.service;

import java.util.List;

import com.lms.management.model.TopicContent;

public interface TopicContentService {

    TopicContent createContent(Long topicId, TopicContent content);

    List<TopicContent> createContentBulk(Long topicId, List<TopicContent> contents);

    List<TopicContent> getAllContents();

    // 🔥 UPDATED: Batch-based access
    TopicContent getContentById(Long contentId, Long batchId);

    // 🔥 UPDATED: Batch-based access
    List<TopicContent> getContentsByTopicId(Long topicId, Long batchId);

    TopicContent updateContent(Long contentId, TopicContent incoming);

    void deleteContent(Long contentId);
}
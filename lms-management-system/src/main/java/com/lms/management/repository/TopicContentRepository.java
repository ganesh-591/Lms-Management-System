package com.lms.management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lms.management.model.TopicContent;

public interface TopicContentRepository
        extends JpaRepository<TopicContent, Long> {

    // ✅ MUST match controller/service usage
    List<TopicContent> findByTopicTopicId(Long topicId);
}

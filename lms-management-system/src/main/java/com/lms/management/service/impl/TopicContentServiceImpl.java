package com.lms.management.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.management.exception.ResourceNotFoundException;
import com.lms.management.model.Topic;
import com.lms.management.model.TopicContent;
import com.lms.management.repository.TopicContentRepository;
import com.lms.management.repository.TopicRepository;
import com.lms.management.service.TopicContentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class TopicContentServiceImpl implements TopicContentService {

    private final TopicContentRepository topicContentRepository;
    private final TopicRepository topicRepository;

    // ===============================
    // 1️⃣ CREATE SINGLE CONTENT (JSON)
    // fileUrl = null allowed
    // ===============================
    @Override
    public TopicContent createContent(Long topicId, TopicContent content) {

        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Topic not found with id: " + topicId
                        )
                );

        content.setTopic(topic);
        return topicContentRepository.save(content);
    }

    // ===============================
    // 2️⃣ CREATE CONTENT BULK (JSON)
    // fileUrl = null allowed
    // ===============================
    @Override
    public List<TopicContent> createContentBulk(
            Long topicId,
            List<TopicContent> contents
    ) {

        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Topic not found with id: " + topicId
                        )
                );

        List<TopicContent> saved = new ArrayList<>();

        for (TopicContent content : contents) {
            content.setTopic(topic);
            saved.add(topicContentRepository.save(content));
        }

        return saved;
    }

    // ===============================
    // 3️⃣ GET CONTENT BY ID
    // ===============================
    @Override
    @Transactional(readOnly = true)
    public TopicContent getContentById(Long contentId) {

        return topicContentRepository.findById(contentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Content not found with id: " + contentId
                        )
                );
    }

    // ===============================
    // 4️⃣ GET CONTENTS BY TOPIC
    // ===============================
    @Override
    @Transactional(readOnly = true)
    public List<TopicContent> getContentsByTopicId(Long topicId) {

        return topicContentRepository.findByTopicTopicId(topicId);
    }

    // ===============================
    // 5️⃣ GET ALL CONTENTS
    // ===============================
    @Override
    @Transactional(readOnly = true)
    public List<TopicContent> getAllContents() {

        return topicContentRepository.findAll();
    }

    // ===============================
    // 6️⃣ UPDATE CONTENT (PUT AS PATCH)
    // 🔒 Used by FILE UPLOAD API
    // ===============================
    @Override
    public TopicContent updateContent(
            Long contentId,
            TopicContent incoming
    ) {

        TopicContent existing = topicContentRepository.findById(contentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Content not found with id: " + contentId
                        )
                );

        // ✅ normal metadata update
        if (incoming.getContentType() != null)
            existing.setContentType(incoming.getContentType());

        if (incoming.getContentOrder() != null)
            existing.setContentOrder(incoming.getContentOrder());

        // ✅ FILE UPLOAD UPDATE (MAIN POINT)
        if (incoming.getFileUrl() != null)
            existing.setFileUrl(incoming.getFileUrl());

        return topicContentRepository.save(existing);
    }

    // ===============================
    // 7️⃣ DELETE CONTENT
    // ===============================
    @Override
    public void deleteContent(Long contentId) {

        TopicContent existing = topicContentRepository.findById(contentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Content not found with id: " + contentId
                        )
                );

        topicContentRepository.delete(existing);
    }
}

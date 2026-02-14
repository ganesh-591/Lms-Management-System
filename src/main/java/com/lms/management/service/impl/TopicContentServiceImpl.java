package com.lms.management.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.management.exception.ResourceNotFoundException;
import com.lms.management.exception.UnauthorizedAccessException;
import com.lms.management.model.Batch;
import com.lms.management.model.Topic;
import com.lms.management.model.TopicContent;
import com.lms.management.repository.TopicContentRepository;
import com.lms.management.repository.TopicRepository;
import com.lms.management.service.BatchService;
import com.lms.management.service.TopicContentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class TopicContentServiceImpl implements TopicContentService {

    private final TopicContentRepository topicContentRepository;
    private final TopicRepository topicRepository;
    private final BatchService batchService;

    // ===============================
    // CREATE SINGLE CONTENT
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

        if (content.getFileUrl() != null && content.getFileUrl().startsWith("http")) {
            content.setContentSource("URL");
        } else {
            content.setContentSource("UPLOAD");
        }

        return topicContentRepository.save(content);
    }

    // ===============================
    // CREATE BULK
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

            if (content.getFileUrl() != null && content.getFileUrl().startsWith("http")) {
                content.setContentSource("URL");
            } else {
                content.setContentSource("UPLOAD");
            }

            saved.add(topicContentRepository.save(content));
        }

        return saved;
    }

    // ===============================
    // GET CONTENT BY ID (Batch-based access)
    // ===============================
    @Override
    @Transactional(readOnly = true)
    public TopicContent getContentById(Long contentId, Long batchId) {

        Batch batch = batchService.getBatchById(batchId);

        if (Boolean.FALSE.equals(batch.getContentAccess())) {
            throw new UnauthorizedAccessException(
                    "Content access disabled for this batch"
            );
        }

        return topicContentRepository.findById(contentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Content not found with id: " + contentId
                        )
                );
    }

    // ===============================
    // GET CONTENTS BY TOPIC (Batch-based access)
    // ===============================
    @Override
    @Transactional(readOnly = true)
    public List<TopicContent> getContentsByTopicId(Long topicId, Long batchId) {

        Batch batch = batchService.getBatchById(batchId);

        if (Boolean.FALSE.equals(batch.getContentAccess())) {
            throw new UnauthorizedAccessException(
                    "Content access disabled for this batch"
            );
        }

        return topicContentRepository.findByTopicTopicId(topicId);
    }

    // ===============================
    // GET ALL CONTENTS (Admin)
    // ===============================
    @Override
    @Transactional(readOnly = true)
    public List<TopicContent> getAllContents() {
        return topicContentRepository.findAll();
    }

    // ===============================
    // UPDATE
    // ===============================
    @Override
    public TopicContent updateContent(Long contentId, TopicContent incoming) {

        TopicContent existing = topicContentRepository.findById(contentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Content not found with id: " + contentId
                        )
                );

        if (incoming.getContentType() != null)
            existing.setContentType(incoming.getContentType());

        if (incoming.getContentTitle() != null)
            existing.setContentTitle(incoming.getContentTitle());

        if (incoming.getContentDescription() != null)
            existing.setContentDescription(incoming.getContentDescription());

        if (incoming.getContentOrder() != null)
            existing.setContentOrder(incoming.getContentOrder());

        if (incoming.getFileUrl() != null)
            existing.setFileUrl(incoming.getFileUrl());

        return topicContentRepository.save(existing);
    }

    // ===============================
    // DELETE
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
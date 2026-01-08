package com.lms.management.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.lms.management.model.TopicContent;
import com.lms.management.service.TopicContentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/topic-contents")
@RequiredArgsConstructor
public class TopicContentController {

    private final TopicContentService topicContentService;

    // ===============================
    // CREATE SINGLE CONTENT
    // ===============================
    @PostMapping("/topic/{topicId}")
    public ResponseEntity<TopicContent> createContent(
            @PathVariable Long topicId,
            @RequestBody TopicContent content) {

        TopicContent created = topicContentService.createContent(topicId, content);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // ===============================
    // CREATE CONTENT IN BULK
    // ===============================
    @PostMapping("/topic/{topicId}/bulk")
    public ResponseEntity<List<TopicContent>> createContentBulk(
            @PathVariable Long topicId,
            @RequestBody List<TopicContent> contents) {

        List<TopicContent> created =
                topicContentService.createContentBulk(topicId, contents);

        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // ===============================
    // ✅ UPDATE CONTENT (THIS WAS MISSING)
    // ===============================
    @PutMapping("/{contentId}")
    public ResponseEntity<TopicContent> updateContent(
            @PathVariable Long contentId,
            @RequestBody TopicContent content) {

        TopicContent updated =
                topicContentService.updateContent(contentId, content);

        return ResponseEntity.ok(updated);
    }

    // ===============================
    // GET CONTENT BY ID
    // ===============================
    @GetMapping("/{contentId}")
    public ResponseEntity<TopicContent> getContentById(
            @PathVariable Long contentId) {

        return ResponseEntity.ok(
                topicContentService.getContentById(contentId)
        );
    }

    // ===============================
    // GET CONTENTS BY TOPIC
    // ===============================
    @GetMapping("/topic/{topicId}")
    public ResponseEntity<List<TopicContent>> getContentsByTopic(
            @PathVariable Long topicId) {

        return ResponseEntity.ok(
                topicContentService.getContentsByTopicId(topicId)
        );
    }

    // ===============================
    // GET ALL CONTENTS
    // ===============================
    @GetMapping
    public ResponseEntity<List<TopicContent>> getAllContents() {
        return ResponseEntity.ok(topicContentService.getAllContents());
    }

    // ===============================
    // DELETE CONTENT
    // ===============================
    @DeleteMapping("/{contentId}")
    public ResponseEntity<Void> deleteContent(
            @PathVariable Long contentId) {

        topicContentService.deleteContent(contentId);
        return ResponseEntity.noContent().build();
    }
}

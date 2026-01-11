package com.lms.management.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.lms.management.model.TopicContent;
import com.lms.management.service.TopicContentService;
import com.lms.management.util.FileUploadUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/topic-contents")
@RequiredArgsConstructor
public class TopicContentController {

    private final TopicContentService topicContentService;

    // ===============================
    // 1️⃣ CREATE SINGLE CONTENT (JSON)
    // ===============================
    @PostMapping("/topic/{topicId}")
    public ResponseEntity<TopicContent> createContent(
            @PathVariable Long topicId,
            @RequestBody TopicContent content) {

        TopicContent created =
                topicContentService.createContent(topicId, content);

        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // ===============================
    // 2️⃣ CREATE CONTENT BULK (JSON)
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
    // 3️⃣ UPLOAD FILES (PUT)
    // ===============================
    @PutMapping("/upload")
    public ResponseEntity<String> uploadContentFiles(
            @RequestParam("contentIds") List<Long> contentIds,
            @RequestParam("files") List<MultipartFile> files
    ) throws IOException {

        if (contentIds.size() != files.size()) {
            return ResponseEntity.badRequest()
                    .body("Files count must match contentIds count");
        }

        for (int i = 0; i < contentIds.size(); i++) {

            String fileUrl =
                    FileUploadUtil.saveTopicContentFile(files.get(i));

            TopicContent update = new TopicContent();
            update.setFileUrl(fileUrl);

            topicContentService.updateContent(contentIds.get(i), update);
        }

        return ResponseEntity.ok("Files uploaded successfully");
    }

    // ===============================
    // ✅ GET ALL CONTENTS (FIX)
    // ===============================
    @GetMapping
    public ResponseEntity<List<TopicContent>> getAllContents() {
        return ResponseEntity.ok(
                topicContentService.getAllContents()
        );
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
    // DELETE CONTENT
    // ===============================
    @DeleteMapping("/{contentId}")
    public ResponseEntity<Void> deleteContent(
            @PathVariable Long contentId) {

        topicContentService.deleteContent(contentId);
        return ResponseEntity.noContent().build();
    }
}

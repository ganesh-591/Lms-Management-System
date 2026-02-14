package com.lms.management.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.lms.management.exception.UnauthorizedAccessException;
import com.lms.management.model.Batch;
import com.lms.management.model.TopicContent;
import com.lms.management.service.BatchService;
import com.lms.management.service.TopicContentService;
import com.lms.management.util.FileUploadUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/topic-contents")
@RequiredArgsConstructor
public class TopicContentController {

    private final TopicContentService topicContentService;
    private final BatchService batchService;

    // ===============================
    // 1️⃣ CREATE SINGLE CONTENT
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
    // 2️⃣ CREATE CONTENT BULK
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
    // 3️⃣ UPDATE CONTENT METADATA
    // ===============================
    @PutMapping("/{contentId}")
    public ResponseEntity<TopicContent> updateContentMeta(
            @PathVariable Long contentId,
            @RequestBody TopicContent content) {

        TopicContent updated =
                topicContentService.updateContent(contentId, content);

        return ResponseEntity.ok(updated);
    }

    // ===============================
    // 4️⃣ UPLOAD FILES (Batch-Based Access)
    // ===============================
    @PutMapping("/upload-files")
    public ResponseEntity<String> uploadContentFiles(
            @RequestParam("batchId") Long batchId,
            @RequestParam("contentIds") List<Long> contentIds,
            @RequestParam("files") List<MultipartFile> files
    ) throws IOException {

        if (contentIds.size() != files.size()) {
            return ResponseEntity.badRequest()
                    .body("Files count must match contentIds count");
        }

        Batch batch = batchService.getBatchById(batchId);

        if (Boolean.FALSE.equals(batch.getContentAccess())) {
            throw new UnauthorizedAccessException(
                    "Content upload disabled for this batch"
            );
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
    // 5️⃣ GET ALL CONTENTS (ADMIN)
    // ===============================
    @GetMapping
    public ResponseEntity<List<TopicContent>> getAllContents() {
        return ResponseEntity.ok(
                topicContentService.getAllContents()
        );
    }

    // ===============================
    // 6️⃣ GET CONTENT BY ID (Batch-based)
    // ===============================
    @GetMapping("/{contentId}")
    public ResponseEntity<TopicContent> getContentById(
            @PathVariable Long contentId,
            @RequestParam("batchId") Long batchId) {

        return ResponseEntity.ok(
                topicContentService.getContentById(contentId, batchId)
        );
    }

    // ===============================
    // 7️⃣ GET CONTENTS BY TOPIC (Batch-based)
    // ===============================
    @GetMapping("/topic/{topicId}")
    public ResponseEntity<List<TopicContent>> getContentsByTopic(
            @PathVariable Long topicId,
            @RequestParam("batchId") Long batchId) {

        return ResponseEntity.ok(
                topicContentService.getContentsByTopicId(topicId, batchId)
        );
    }

    // ===============================
    // 8️⃣ DELETE CONTENT
    // ===============================
    @DeleteMapping("/{contentId}")
    public ResponseEntity<Void> deleteContent(
            @PathVariable Long contentId) {

        topicContentService.deleteContent(contentId);
        return ResponseEntity.noContent().build();
    }
}
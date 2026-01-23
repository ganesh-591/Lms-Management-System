package com.lms.management.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.lms.management.exception.ResourceNotFoundException;
import com.lms.management.exception.UnauthorizedAccessException;
import com.lms.management.model.SessionContent;
import com.lms.management.service.SessionContentService;
import com.lms.management.util.FileUploadUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/session-contents")
@RequiredArgsConstructor
public class SessionContentController {

    private final SessionContentService sessionContentService;

    // ================= CREATE METADATA =================
    @PostMapping("/session/{sessionId}")
    public ResponseEntity<SessionContent> createSessionContent(
            @PathVariable Long sessionId,
            @RequestBody SessionContent sessionContent) {

        requirePermission("SESSION_CONTENT_CREATE");

        SessionContent created =
                sessionContentService.createSessionContent(sessionId, sessionContent);

        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // ================= GET BY ID =================
    @GetMapping("/{sessionContentId}")
    public ResponseEntity<SessionContent> getSessionContentById(
            @PathVariable Long sessionContentId) {

        requirePermission("SESSION_CONTENT_VIEW");

        return ResponseEntity.ok(
                sessionContentService.getSessionContentById(sessionContentId)
        );
    }

    // ================= GET BY SESSION =================
    @GetMapping("/session/{sessionId}")
    public ResponseEntity<List<SessionContent>> getContentsBySession(
            @PathVariable Long sessionId) {

        requirePermission("SESSION_CONTENT_VIEW");

        return ResponseEntity.ok(
                sessionContentService.getContentsBySessionId(sessionId)
        );
    }

    // ================= UPLOAD FILE =================
    @PutMapping(
        value = "/{sessionContentId}/upload",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<SessionContent> uploadSessionContentFile(
            @PathVariable Long sessionContentId,
            @RequestParam("file") MultipartFile file) throws IOException {

        requirePermission("SESSION_CONTENT_UPDATE");

        SessionContent content =
                sessionContentService.getSessionContentById(sessionContentId);

        String fileUrl = FileUploadUtil.saveSessionContentFile(file);
        content.setFileUrl(fileUrl);

        return ResponseEntity.ok(
                sessionContentService.updateSessionContent(sessionContentId, content)
        );
    }

    // ================= UPDATE METADATA (PUT = PATCH) =================
    @PutMapping("/{sessionContentId}")
    public ResponseEntity<SessionContent> updateSessionContent(
            @PathVariable Long sessionContentId,
            @RequestBody SessionContent updatedContent) {

        requirePermission("SESSION_CONTENT_UPDATE");

        return ResponseEntity.ok(
                sessionContentService.updateSessionContent(
                        sessionContentId, updatedContent)
        );
    }

    // ================= DELETE =================
    @DeleteMapping("/{sessionContentId}")
    public ResponseEntity<Void> deleteSessionContent(
            @PathVariable Long sessionContentId) {

        requirePermission("SESSION_CONTENT_DELETE");

        sessionContentService.deleteSessionContent(sessionContentId);
        return ResponseEntity.noContent().build();
    }

    // ================= PREVIEW =================
    @GetMapping("/preview/{sessionContentId}")
    public ResponseEntity<Resource> previewSessionContent(
            @PathVariable Long sessionContentId) throws IOException {

        requirePermission("SESSION_CONTENT_VIEW");

        SessionContent content =
                sessionContentService.getSessionContentById(sessionContentId);

        if (content.getFileUrl() == null) {
            throw new ResourceNotFoundException("File not uploaded yet");
        }

        Path filePath = Paths.get(content.getFileUrl().substring(1));
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists()) {
            throw new ResourceNotFoundException("File not found");
        }

        String contentType = Files.probeContentType(filePath);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .body(resource);
    }

    // ================= DOWNLOAD =================
    @GetMapping("/download/{sessionContentId}")
    public ResponseEntity<Resource> downloadSessionContent(
            @PathVariable Long sessionContentId) throws IOException {

        requirePermission("SESSION_CONTENT_DOWNLOAD");

        SessionContent content =
                sessionContentService.getSessionContentById(sessionContentId);

        if (content.getFileUrl() == null) {
            throw new ResourceNotFoundException("File not uploaded yet");
        }

        Path filePath = Paths.get(content.getFileUrl().substring(1));
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists()) {
            throw new ResourceNotFoundException("File not found");
        }

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\""
                )
                .body(resource);
    }

    // ================= PERMISSION CHECK =================
    private void requirePermission(String permission) {

        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getAuthorities().stream()
                .noneMatch(a -> a.getAuthority().equals(permission))) {

            throw new UnauthorizedAccessException(
                    "Missing permission: " + permission
            );
        }
    }
}

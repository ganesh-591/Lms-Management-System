package com.lms.management.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.management.exception.ResourceNotFoundException;
import com.lms.management.model.SessionContent;
import com.lms.management.repository.SessionContentRepository;
import com.lms.management.service.SessionContentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SessionContentServiceImpl implements SessionContentService {

    private final SessionContentRepository sessionContentRepository;

    // ================= CREATE =================
    @Override
    public SessionContent createSessionContent(Long sessionId, SessionContent sessionContent) {

        // enforce sessionId from URL
        sessionContent.setSessionId(sessionId);

        return sessionContentRepository.save(sessionContent);
    }

    // ================= GET BY ID =================
    @Override
    public SessionContent getSessionContentById(Long sessionContentId) {

        return sessionContentRepository.findById(sessionContentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Session content not found"));
    }

    // ================= GET BY SESSION =================
    @Override
    public List<SessionContent> getContentsBySessionId(Long sessionId) {

        return sessionContentRepository.findBySessionIdAndStatus(
                sessionId, "ACTIVE");
    }

    // ================= UPDATE (PUT = PATCH) =================
    @Override
    public SessionContent updateSessionContent(
            Long sessionContentId,
            SessionContent updatedContent) {

        SessionContent existingContent = getSessionContentById(sessionContentId);

        if (updatedContent.getTitle() != null) {
            existingContent.setTitle(updatedContent.getTitle());
        }

        if (updatedContent.getDescription() != null) {
            existingContent.setDescription(updatedContent.getDescription());
        }

        if (updatedContent.getContentType() != null) {
            existingContent.setContentType(updatedContent.getContentType());
        }

        if (updatedContent.getFileUrl() != null) {
            existingContent.setFileUrl(updatedContent.getFileUrl());
        }

        if (updatedContent.getStatus() != null) {
            existingContent.setStatus(updatedContent.getStatus());
        }

        return sessionContentRepository.save(existingContent);
    }

    // ================= DELETE =================
    @Override
    public void deleteSessionContent(Long sessionContentId) {

        SessionContent content = getSessionContentById(sessionContentId);
        sessionContentRepository.delete(content);
    }
}

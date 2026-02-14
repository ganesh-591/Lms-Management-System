package com.lms.management.service;

import java.util.List;

import com.lms.management.model.SessionContent;

public interface SessionContentService {

    // Create session content metadata (admin)
    SessionContent createSessionContent(Long sessionId, SessionContent sessionContent);

    // 🔒 Get content by ID (batch-based access)
    SessionContent getSessionContentById(Long sessionContentId, Long batchId);

    // 🔒 Get all contents for a session (batch-based access)
    List<SessionContent> getContentsBySessionId(Long sessionId, Long batchId);

    // Update content metadata
    SessionContent updateSessionContent(Long sessionContentId, SessionContent sessionContent);

    // Delete content
    void deleteSessionContent(Long sessionContentId);
}
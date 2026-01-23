package com.lms.management.service;

import java.util.List;

import com.lms.management.model.SessionContent;

public interface SessionContentService {

    // Create session content metadata (file uploaded later)
    SessionContent createSessionContent(Long sessionId, SessionContent sessionContent);

    // Get content by ID
    SessionContent getSessionContentById(Long sessionContentId);

    // Get all contents for a session
    List<SessionContent> getContentsBySessionId(Long sessionId);

    // Update content metadata (PUT = PATCH)
    SessionContent updateSessionContent(Long sessionContentId, SessionContent sessionContent);

    // Delete content
    void deleteSessionContent(Long sessionContentId);
}

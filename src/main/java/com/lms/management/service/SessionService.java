package com.lms.management.service;

import java.util.List;

import com.lms.management.model.Session;

public interface SessionService {

    // Create a session under a batch
    Session createSession(Long batchId, Session session);

    // Get session by ID
    Session getSessionById(Long sessionId);

    // Get all sessions for a batch
    List<Session> getSessionsByBatchId(Long batchId);

    // Update session (PUT = PATCH)
    Session updateSession(Long sessionId, Session session);

    // Delete session
    void deleteSession(Long sessionId);
}

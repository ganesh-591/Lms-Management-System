package com.lms.management.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.management.exception.ResourceNotFoundException;
import com.lms.management.exception.UnauthorizedAccessException;
import com.lms.management.model.Batch;
import com.lms.management.model.Session;
import com.lms.management.model.SessionContent;
import com.lms.management.repository.SessionContentRepository;
import com.lms.management.repository.SessionRepository;
import com.lms.management.service.BatchService;
import com.lms.management.service.SessionContentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SessionContentServiceImpl implements SessionContentService {

    private final SessionContentRepository sessionContentRepository;
    private final SessionRepository sessionRepository;
    private final BatchService batchService;

    // ================= CREATE (BLOCK IF ACCESS FALSE) =================
    @Override
    public SessionContent createSessionContent(Long sessionId, SessionContent sessionContent) {

        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Session not found"));

        Batch batch = batchService.getBatchById(session.getBatchId());

        if (Boolean.FALSE.equals(batch.getContentAccess())) {
            throw new UnauthorizedAccessException(
                    "Session content creation disabled for this batch"
            );
        }

        sessionContent.setSessionId(sessionId);
        return sessionContentRepository.save(sessionContent);
    }

    // ================= GET BY ID =================
    @Override
    @Transactional(readOnly = true)
    public SessionContent getSessionContentById(Long sessionContentId, Long batchId) {

        Batch batch = batchService.getBatchById(batchId);

        if (Boolean.FALSE.equals(batch.getContentAccess())) {
            throw new UnauthorizedAccessException(
                    "Session content access disabled for this batch"
            );
        }

        SessionContent content = sessionContentRepository.findById(sessionContentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Session content not found"));

        Session session = sessionRepository.findById(content.getSessionId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Session not found"));

        if (!session.getBatchId().equals(batchId)) {
            throw new UnauthorizedAccessException(
                    "This content does not belong to your batch"
            );
        }

        return content;
    }

    // ================= GET BY SESSION =================
    @Override
    @Transactional(readOnly = true)
    public List<SessionContent> getContentsBySessionId(Long sessionId, Long batchId) {

        Batch batch = batchService.getBatchById(batchId);

        if (Boolean.FALSE.equals(batch.getContentAccess())) {
            throw new UnauthorizedAccessException(
                    "Session content access disabled for this batch"
            );
        }

        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Session not found"));

        if (!session.getBatchId().equals(batchId)) {
            throw new UnauthorizedAccessException(
                    "This session does not belong to your batch"
            );
        }

        return sessionContentRepository.findBySessionIdAndStatus(
                sessionId, "ACTIVE");
    }

    // ================= UPDATE =================
    @Override
    public SessionContent updateSessionContent(
            Long sessionContentId,
            SessionContent updatedContent) {

        SessionContent existing =
                sessionContentRepository.findById(sessionContentId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Session content not found"));

        if (updatedContent.getTitle() != null)
            existing.setTitle(updatedContent.getTitle());

        if (updatedContent.getDescription() != null)
            existing.setDescription(updatedContent.getDescription());

        if (updatedContent.getContentType() != null)
            existing.setContentType(updatedContent.getContentType());

        if (updatedContent.getFileUrl() != null)
            existing.setFileUrl(updatedContent.getFileUrl());

        if (updatedContent.getStatus() != null)
            existing.setStatus(updatedContent.getStatus());

        return sessionContentRepository.save(existing);
    }

    // ================= DELETE =================
    @Override
    public void deleteSessionContent(Long sessionContentId) {

        SessionContent content = sessionContentRepository.findById(sessionContentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Session content not found"));

        sessionContentRepository.delete(content);
    }
}
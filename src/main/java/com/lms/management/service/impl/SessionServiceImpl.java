package com.lms.management.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.management.exception.ResourceNotFoundException;
import com.lms.management.model.Batch;
import com.lms.management.model.Session;
import com.lms.management.repository.BatchRepository;
import com.lms.management.repository.SessionRepository;
import com.lms.management.service.SessionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SessionServiceImpl implements SessionService {

    private final SessionRepository sessionRepository;
    private final BatchRepository batchRepository; // ✅ added

    // ================= CREATE =================
    @Override
    public Session createSession(Long batchId, Session session) {

        // 🔹 Load batch (source of truth)
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Batch not found"));

        // 🔹 Force-set relations
        session.setBatchId(batchId);
        session.setCourseId(batch.getCourseId()); // ✅ CRITICAL FIX

        return sessionRepository.save(session);
    }

    // ================= GET BY ID =================
    @Override
    public Session getSessionById(Long sessionId) {

        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Session not found"));

        session.setStatus(calculateStatus(session));
        return session;
    }

    // ================= GET BY BATCH =================
    @Override
    public List<Session> getSessionsByBatchId(Long batchId) {

        List<Session> sessions = sessionRepository.findByBatchId(batchId);

        sessions.forEach(session ->
                session.setStatus(calculateStatus(session)));

        return sessions;
    }

    // ================= UPDATE (PUT = PATCH) =================
    @Override
    public Session updateSession(Long sessionId, Session updatedSession) {

        Session existingSession = getSessionById(sessionId);

        if (updatedSession.getSessionName() != null) {
            existingSession.setSessionName(updatedSession.getSessionName());
        }

        if (updatedSession.getStartDate() != null) {
            existingSession.setStartDate(updatedSession.getStartDate());
        }

        if (updatedSession.getStartTime() != null) {
            existingSession.setStartTime(updatedSession.getStartTime());
        }

        if (updatedSession.getDurationMinutes() != null) {
            existingSession.setDurationMinutes(updatedSession.getDurationMinutes());
        }

        if (updatedSession.getDays() != null) {
            existingSession.setDays(updatedSession.getDays());
        }

        if (updatedSession.getSessionType() != null) {
            existingSession.setSessionType(updatedSession.getSessionType());
        }

        if (updatedSession.getMeetingLink() != null) {
            existingSession.setMeetingLink(updatedSession.getMeetingLink());
        }

        // ❌ Status is NOT manually updated
        return sessionRepository.save(existingSession);
    }

    // ================= DELETE =================
    @Override
    public void deleteSession(Long sessionId) {
        Session session = getSessionById(sessionId);
        sessionRepository.delete(session);
    }

    // ================= STATUS CALCULATION =================
    private String calculateStatus(Session session) {

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime startDateTime =
                LocalDateTime.of(
                        session.getStartDate(),
                        session.getStartTime()
                );

        LocalDateTime endDateTime =
                startDateTime.plusMinutes(
                        session.getDurationMinutes()
                );

        if (now.isBefore(startDateTime)) {
            return "Upcoming";
        }

        if (now.isAfter(startDateTime) && now.isBefore(endDateTime)) {
            return "Running";
        }

        return "Completed";
    }
}

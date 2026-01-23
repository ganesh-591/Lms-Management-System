package com.lms.management.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.lms.management.exception.UnauthorizedAccessException;
import com.lms.management.model.Session;
import com.lms.management.service.SessionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    // ================= CREATE =================
    @PostMapping("/batch/{batchId}")
    public ResponseEntity<Session> createSession(
            @PathVariable Long batchId,
            @RequestBody Session session) {

        requirePermission("SESSION_CREATE");

        Session createdSession = sessionService.createSession(batchId, session);
        return new ResponseEntity<>(createdSession, HttpStatus.CREATED);
    }

    // ================= GET BY ID =================
    @GetMapping("/{sessionId}")
    public ResponseEntity<Session> getSessionById(
            @PathVariable Long sessionId) {

        requirePermission("SESSION_VIEW");

        return ResponseEntity.ok(sessionService.getSessionById(sessionId));
    }

    // ================= GET BY BATCH =================
    @GetMapping("/batch/{batchId}")
    public ResponseEntity<List<Session>> getSessionsByBatchId(
            @PathVariable Long batchId) {

        requirePermission("SESSION_VIEW");

        return ResponseEntity.ok(sessionService.getSessionsByBatchId(batchId));
    }

    // ================= UPDATE (PUT = PATCH) =================
    @PutMapping("/{sessionId}")
    public ResponseEntity<Session> updateSession(
            @PathVariable Long sessionId,
            @RequestBody Session session) {

        requirePermission("SESSION_UPDATE");

        return ResponseEntity.ok(
                sessionService.updateSession(sessionId, session)
        );
    }

    // ================= DELETE =================
    @DeleteMapping("/{sessionId}")
    public ResponseEntity<Void> deleteSession(
            @PathVariable Long sessionId) {

        requirePermission("SESSION_DELETE");

        sessionService.deleteSession(sessionId);
        return ResponseEntity.noContent().build();
    }

    // ================= PERMISSION CHECK =================
    private void requirePermission(String permission) {

        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (auth == null || auth.getAuthorities().stream()
                .noneMatch(a -> a.getAuthority().equals(permission))) {

            throw new UnauthorizedAccessException(
                    "Missing permission: " + permission
            );
        }
    }
}

package com.lms.management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.management.model.SessionContent;

@Repository
public interface SessionContentRepository extends JpaRepository<SessionContent, Long> {

    // Get all contents for a session
    List<SessionContent> findBySessionId(Long sessionId);

    // Get only ACTIVE contents for a session
    List<SessionContent> findBySessionIdAndStatus(Long sessionId, String status);
}

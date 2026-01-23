package com.lms.management.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.management.model.AttendanceSession;

@Repository
public interface AttendanceSessionRepository
        extends JpaRepository<AttendanceSession, Long> {

    // ✅ Ensure only one ACTIVE attendance per session
    Optional<AttendanceSession> findBySessionIdAndStatus(
            Long sessionId,
            String status
    );
}

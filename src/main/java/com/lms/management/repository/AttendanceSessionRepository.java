package com.lms.management.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    // ✅ Get attendance sessions by DATE (using startedAt)
    @Query("""
        SELECT a
        FROM AttendanceSession a
        WHERE DATE(a.startedAt) = :date
    """)
    List<AttendanceSession> findByDate(
            @Param("date") LocalDate date
    );

    List<AttendanceSession> findByStartedAtBetween(
            LocalDateTime start,
            LocalDateTime end
    );

    // ===============================
    // ➕ ADDON (ENDED + ACTIVE TOGETHER)
    // ===============================
    List<AttendanceSession> findBySessionIdAndStatusInOrderByStartedAtDesc(
            Long sessionId,
            List<String> statuses
    );
}

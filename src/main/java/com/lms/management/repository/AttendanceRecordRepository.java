package com.lms.management.repository;

import java.awt.print.Pageable;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.management.model.AttendanceRecord;

@Repository
public interface AttendanceRecordRepository
        extends JpaRepository<AttendanceRecord, Long> {

    // ===============================
    // BASIC
    // ===============================
    List<AttendanceRecord> findByAttendanceSessionId(Long attendanceSessionId);

    Optional<AttendanceRecord> findByAttendanceSessionIdAndStudentId(
            Long attendanceSessionId,
            Long studentId
    );

    List<AttendanceRecord> findByStudentId(Long studentId);

    // ===============================
    // DATE BASED (REPORTS / DASHBOARD)
    // ===============================
    List<AttendanceRecord> findByAttendanceDate(LocalDate attendanceDate);

    List<AttendanceRecord> findByAttendanceSessionIdAndAttendanceDate(
            Long attendanceSessionId,
            LocalDate attendanceDate
    );
    
    List<AttendanceRecord>
    findTopNByStudentIdOrderByAttendanceDateDesc(Long studentId, Pageable pageable);
    
    List<AttendanceRecord> findTopByStudentIdOrderByAttendanceDateDesc(
            Long studentId,
            int limit
    );
    long countByStudentIdAndAttendanceSessionIdIn(
            Long studentId,
            List<Long> sessionIds
    );

    long countByStudentIdAndAttendanceSessionIdInAndStatusIn(
            Long studentId,
            List<Long> sessionIds,
            List<String> statuses
    );
}

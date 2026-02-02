package com.lms.management.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.lms.management.model.AttendanceRecord;

@Repository
public interface AttendanceRecordRepository
        extends JpaRepository<AttendanceRecord, Long> {

    List<AttendanceRecord> findByAttendanceSessionId(Long attendanceSessionId);

    Optional<AttendanceRecord> findByAttendanceSessionIdAndStudentId(
            Long attendanceSessionId,
            Long studentId
    );

    List<AttendanceRecord> findByStudentId(Long studentId);

    List<AttendanceRecord> findByStudentIdAndAttendanceSessionIdIn(
            Long studentId,
            List<Long> attendanceSessionIds
    );

    long countByStudentIdAndAttendanceSessionIdInAndStatusIn(
            Long studentId,
            List<Long> attendanceSessionIds,
            List<String> statuses
    );

    List<AttendanceRecord>
    findByStudentIdOrderByAttendanceDateDesc(
            Long studentId,
            Pageable pageable
    );
    
    List<AttendanceRecord> findByAttendanceDate(LocalDate attendanceDate);

    List<AttendanceRecord> findByAttendanceSessionIdAndAttendanceDate(
            Long attendanceSessionId,
            LocalDate attendanceDate
    );
    
    
    List<AttendanceRecord>
    findTopByStudentIdOrderByAttendanceDateDesc(
            Long studentId,
            Pageable pageable
    );
    /*List<AttendanceRecord>
    findTopNByStudentIdOrderByAttendanceDateDesc(
            Long studentId,
            int limit
    );*/
    	
    @Query("""
        SELECT ar.studentId
        FROM AttendanceRecord ar
        WHERE ar.attendanceSessionId = :sessionId
    """)
    List<Long> findStudentIdsByAttendanceSessionId(
            @Param("sessionId") Long sessionId
    );
}

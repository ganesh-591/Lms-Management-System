package com.lms.management.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.management.model.AttendanceRecord;

@Repository
public interface AttendanceRecordRepository
        extends JpaRepository<AttendanceRecord, Long> {

    // ✅ Get all records for an attendance session
    List<AttendanceRecord> findByAttendanceSessionId(Long attendanceSessionId);

    // ✅ Get a student's record for a session (prevent duplicates)
    Optional<AttendanceRecord> findByAttendanceSessionIdAndStudentId(
            Long attendanceSessionId,
            Long studentId
    );

    // ✅ Get all records of a student (self view)
    List<AttendanceRecord> findByStudentId(Long studentId);
}

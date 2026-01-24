package com.lms.management.service;

import java.time.LocalDate;
import java.util.List;

import com.lms.management.model.AttendanceSession;

public interface AttendanceSessionService {

    // ===============================
    // START / END ATTENDANCE
    // ===============================
    AttendanceSession startAttendance(
            Long sessionId,
            Long courseId,
            Long batchId,
            Long userId
    );

    AttendanceSession endAttendance(Long attendanceSessionId);

    // ===============================
    // BASIC FETCH
    // ===============================
    AttendanceSession getById(Long attendanceSessionId);

    AttendanceSession getActiveBySessionId(Long sessionId);

    // ===============================
    // DATE BASED
    // ===============================
    List<AttendanceSession> getByDate(LocalDate date);

    // ===============================
    // ➕ ACTIVE + ENDED (SINGLE API)
    // ===============================
    List<AttendanceSession> getActiveAndEndedBySessionId(Long sessionId);

    // ===============================
    // DELETE
    // ===============================
    void delete(Long attendanceSessionId);
}

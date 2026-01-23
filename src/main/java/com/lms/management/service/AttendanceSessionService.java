package com.lms.management.service;

import com.lms.management.model.AttendanceSession;

public interface AttendanceSessionService {

    // Start attendance for a session
    AttendanceSession startAttendance(
            Long sessionId,
            Long courseId,
            Long batchId,
            Long userId
    );

    // End attendance
    AttendanceSession endAttendance(Long attendanceSessionId);

    // Get attendance session by id
    AttendanceSession getById(Long attendanceSessionId);

    // Get ACTIVE attendance session for a session
    AttendanceSession getActiveBySessionId(Long sessionId);
}

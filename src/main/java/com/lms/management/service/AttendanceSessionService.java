package com.lms.management.service;

import java.time.LocalDate;
import java.util.List;

import com.lms.management.model.AttendanceSession;

public interface AttendanceSessionService {

    AttendanceSession startAttendance(
            Long sessionId,
            Long courseId,
            Long batchId,
            Long userId
    );

    AttendanceSession endAttendance(Long attendanceSessionId);

    AttendanceSession getById(Long attendanceSessionId);

    AttendanceSession getActiveBySessionId(Long sessionId);

    List<AttendanceSession> getByDate(LocalDate date);
    void delete(Long attendanceSessionId);
}

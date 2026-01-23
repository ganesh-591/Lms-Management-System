package com.lms.management.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.management.exception.ResourceNotFoundException;
import com.lms.management.model.AttendanceSession;
import com.lms.management.repository.AttendanceSessionRepository;
import com.lms.management.service.AttendanceSessionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AttendanceSessionServiceImpl implements AttendanceSessionService {

    private final AttendanceSessionRepository attendanceSessionRepository;

    // ===============================
    // START ATTENDANCE
    // ===============================
    @Override
    public AttendanceSession startAttendance(
            Long sessionId,
            Long courseId,
            Long batchId,
            Long userId
    ) {

        // prevent duplicate ACTIVE attendance
        attendanceSessionRepository
                .findBySessionIdAndStatus(sessionId, "ACTIVE")
                .ifPresent(a -> {
                    throw new IllegalStateException(
                            "Attendance already started for this session"
                    );
                });

        AttendanceSession attendanceSession = new AttendanceSession();
        attendanceSession.setSessionId(sessionId);
        attendanceSession.setCourseId(courseId);
        attendanceSession.setBatchId(batchId);
        attendanceSession.setCreatedBy(userId);

        return attendanceSessionRepository.save(attendanceSession);
    }

    // ===============================
    // END ATTENDANCE
    // ===============================
    @Override
    public AttendanceSession endAttendance(Long attendanceSessionId) {

        AttendanceSession attendanceSession =
                attendanceSessionRepository.findById(attendanceSessionId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Attendance session not found"
                                )
                        );

        attendanceSession.setStatus("ENDED");
        attendanceSession.setEndedAt(java.time.LocalDateTime.now());

        return attendanceSessionRepository.save(attendanceSession);
    }

    // ===============================
    // GET BY ID
    // ===============================
    @Override
    @Transactional(readOnly = true)
    public AttendanceSession getById(Long attendanceSessionId) {

        return attendanceSessionRepository.findById(attendanceSessionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Attendance session not found"
                        )
                );
    }

    // ===============================
    // GET ACTIVE BY SESSION ID
    // ===============================
    @Override
    @Transactional(readOnly = true)
    public AttendanceSession getActiveBySessionId(Long sessionId) {

        return attendanceSessionRepository
                .findBySessionIdAndStatus(sessionId, "ACTIVE")
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No active attendance for this session"
                        )
                );
    }
}

package com.lms.management.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.lms.management.model.AttendanceSession;
import com.lms.management.service.AttendanceSessionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/attendance/session")
@RequiredArgsConstructor
public class AttendanceSessionController {

    private final AttendanceSessionService attendanceSessionService;

    // ===============================
    // START ATTENDANCE
    // ===============================
    @PostMapping("/start")
    @PreAuthorize("hasAuthority('ATTENDANCE_START')")
    public AttendanceSession startAttendance(
            @RequestParam Long sessionId,
            @RequestParam Long courseId,
            @RequestParam Long batchId,
            @RequestParam Long userId
    ) {
        return attendanceSessionService.startAttendance(
                sessionId,
                courseId,
                batchId,
                userId
        );
    }

    // ===============================
    // END ATTENDANCE
    // ===============================
    @PutMapping("/{attendanceSessionId}/end")
    @PreAuthorize("hasAuthority('ATTENDANCE_END')")
    public AttendanceSession endAttendance(
            @PathVariable Long attendanceSessionId
    ) {
        return attendanceSessionService.endAttendance(attendanceSessionId);
    }

    // ===============================
    // GET BY ID
    // ===============================
    @GetMapping("/{attendanceSessionId}")
    @PreAuthorize("hasAuthority('ATTENDANCE_VIEW')")
    public AttendanceSession getById(
            @PathVariable Long attendanceSessionId
    ) {
        return attendanceSessionService.getById(attendanceSessionId);
    }

    // ===============================
    // GET ACTIVE BY SESSION
    // ===============================
    @GetMapping("/active/{sessionId}")
    @PreAuthorize("hasAuthority('ATTENDANCE_VIEW')")
    public AttendanceSession getActiveBySession(
            @PathVariable Long sessionId
    ) {
        return attendanceSessionService.getActiveBySessionId(sessionId);
    }
}

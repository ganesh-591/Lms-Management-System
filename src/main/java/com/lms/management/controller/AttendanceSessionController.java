package com.lms.management.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
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
            @RequestParam(name = "sessionId") Long sessionId,
            @RequestParam(name = "courseId") Long courseId,
            @RequestParam(name = "batchId") Long batchId,
            @RequestParam(name = "userId") Long userId
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

    // ===============================
    // ✅ GET BY DATE (PATH VARIABLE)
    // ===============================
    @GetMapping("/date/{date}")
    @PreAuthorize("hasAuthority('ATTENDANCE_VIEW')")
    public List<AttendanceSession> getByDate(
            @PathVariable
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        return attendanceSessionService.getByDate(date);
    }

    // ===============================
    // DELETE ATTENDANCE SESSION
    // ===============================
    @DeleteMapping("/{attendanceSessionId}")
    @PreAuthorize("hasAuthority('ATTENDANCE_DELETE')")
    public void deleteAttendanceSession(
            @PathVariable Long attendanceSessionId
    ) {
        attendanceSessionService.delete(attendanceSessionId);
    }
}

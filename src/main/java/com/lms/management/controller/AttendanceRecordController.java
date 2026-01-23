package com.lms.management.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.lms.management.model.AttendanceRecord;
import com.lms.management.service.AttendanceRecordService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/attendance/record")
@RequiredArgsConstructor
public class AttendanceRecordController {

    private final AttendanceRecordService attendanceRecordService;

    // ===============================
    // MARK ATTENDANCE (SINGLE)
    // ===============================
    @PostMapping
    @PreAuthorize("hasAuthority('ATTENDANCE_RECORD_CREATE')")
    public AttendanceRecord markAttendance(
            @RequestBody AttendanceRecord record
    ) {
        return attendanceRecordService.markAttendance(record);
    }

    // ===============================
    // ✅ MARK ATTENDANCE (BULK)
    // ===============================
    @PostMapping("/bulk")
    @PreAuthorize("hasAuthority('ATTENDANCE_RECORD_CREATE')")
    public List<AttendanceRecord> markAttendanceBulk(
            @RequestBody List<AttendanceRecord> records
    ) {
        return attendanceRecordService.markAttendanceBulk(records);
    }

    // ===============================
    // UPDATE ATTENDANCE (PUT = PATCH)
    // ===============================
    @PutMapping("/{attendanceRecordId}")
    @PreAuthorize("hasAuthority('ATTENDANCE_RECORD_UPDATE')")
    public AttendanceRecord updateAttendance(
            @PathVariable Long attendanceRecordId,
            @RequestBody AttendanceRecord incoming
    ) {
        return attendanceRecordService.updateAttendance(
                attendanceRecordId,
                incoming
        );
    }

    // ===============================
    // GET BY ATTENDANCE SESSION
    // ===============================
    @GetMapping("/session/{attendanceSessionId}")
    @PreAuthorize("hasAuthority('ATTENDANCE_RECORD_VIEW')")
    public List<AttendanceRecord> getByAttendanceSession(
            @PathVariable Long attendanceSessionId
    ) {
        return attendanceRecordService.getByAttendanceSession(
                attendanceSessionId
        );
    }

    // ===============================
    // STUDENT SELF VIEW
    // ===============================
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAuthority('ATTENDANCE_RECORD_SELF_VIEW')")
    public List<AttendanceRecord> getByStudent(
            @PathVariable Long studentId
    ) {
        return attendanceRecordService.getByStudent(studentId);
    }

    // ===============================
    // DELETE ATTENDANCE RECORD
    // ===============================
    @DeleteMapping("/{attendanceRecordId}")
    @PreAuthorize("hasAuthority('ATTENDANCE_RECORD_DELETE')")
    public void deleteAttendanceRecord(
            @PathVariable Long attendanceRecordId
    ) {
        attendanceRecordService.delete(attendanceRecordId);
    }
}

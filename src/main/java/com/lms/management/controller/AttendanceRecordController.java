package com.lms.management.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
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
    // MARK ATTENDANCE (BULK)
    // ===============================
    @PostMapping("/bulk")
    @PreAuthorize("hasAuthority('ATTENDANCE_RECORD_CREATE')")
    public List<AttendanceRecord> markAttendanceBulk(
            @RequestBody List<AttendanceRecord> records
    ) {
        return attendanceRecordService.markAttendanceBulk(records);
    }

    // ===============================
    // UPDATE ATTENDANCE (PUT)
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
    // GET BY DATE (DASHBOARD / REPORTS)
    // ===============================
    @GetMapping("/date/{date}")
    @PreAuthorize("hasAuthority('ATTENDANCE_RECORD_VIEW')")
    public List<AttendanceRecord> getByDate(
            @PathVariable
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        return attendanceRecordService.getByDate(date);
    }

    // ===============================
    // GET BY SESSION + DATE (REPORTS)
    // ===============================
    @GetMapping("/session/{attendanceSessionId}/date/{date}")
    @PreAuthorize("hasAuthority('ATTENDANCE_RECORD_VIEW')")
    public List<AttendanceRecord> getBySessionAndDate(
            @PathVariable Long attendanceSessionId,
            @PathVariable
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        return attendanceRecordService.getBySessionAndDate(
                attendanceSessionId,
                date
        );
    }

    // ===============================
    // ADMIN / INSTRUCTOR VIEW BY STUDENT
    // ===============================
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAuthority('ATTENDANCE_RECORD_VIEW')")
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
    
 // MARK LEAVE (STUDENT LEAVES SESSION)
 // ===============================
 @PostMapping("/leave")
 @PreAuthorize("hasAuthority('ATTENDANCE_RECORD_CREATE')")
 public void markLeave(
         @RequestParam Long attendanceSessionId,
         @RequestParam Long studentId
 ) {
     attendanceRecordService.markLeave(
             attendanceSessionId,
             studentId
     );
 }
 
 
}

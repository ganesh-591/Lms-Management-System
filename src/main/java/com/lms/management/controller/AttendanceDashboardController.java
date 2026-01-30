package com.lms.management.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.lms.management.model.StudentAttendanceStatus;
import com.lms.management.service.AttendanceRecordService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/attendance/dashboard")
@RequiredArgsConstructor
public class AttendanceDashboardController {

    private final AttendanceRecordService attendanceRecordService;

    @GetMapping
    @PreAuthorize("hasAuthority('ATTENDANCE_RECORD_VIEW')")
    public List<StudentAttendanceStatus> getDashboardStatus(
            @RequestParam Long courseId,
            @RequestParam Long batchId
    ) {
        return attendanceRecordService
                .getDashboardAttendanceStatus(courseId, batchId);
    }
}

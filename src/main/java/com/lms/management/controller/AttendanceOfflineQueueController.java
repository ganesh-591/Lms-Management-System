package com.lms.management.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.lms.management.model.AttendanceOfflineQueue;
import com.lms.management.service.AttendanceOfflineQueueService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/attendance/offline")
@RequiredArgsConstructor
public class AttendanceOfflineQueueController {

    private final AttendanceOfflineQueueService attendanceOfflineQueueService;

    /**
     * 🔹 Get all pending offline attendance records
     * Used by: Offline Upload / Sync Queue screen
     */
    @GetMapping("/pending")
    @PreAuthorize("hasAuthority('ATTENDANCE_VIEW')")
    public ResponseEntity<List<AttendanceOfflineQueue>> getPendingOfflineRecords() {

        List<AttendanceOfflineQueue> records =
                attendanceOfflineQueueService.getPendingOfflineRecords();

        return ResponseEntity.ok(records);
    }

    /**
     * 🔹 Sync all pending offline attendance records
     * Triggered by: Sync Queue button
     */
    @PostMapping("/sync")
    @PreAuthorize("hasAuthority('ATTENDANCE_CREATE')")
    public ResponseEntity<String> syncOfflineAttendance() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        Long userId = (Long) authentication.getPrincipal();

        attendanceOfflineQueueService.syncOfflineAttendance(userId);

        return ResponseEntity.ok("Offline attendance synced successfully");
    }
}

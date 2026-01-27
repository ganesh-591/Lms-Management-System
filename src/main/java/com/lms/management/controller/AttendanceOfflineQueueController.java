package com.lms.management.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.lms.management.model.AttendanceOfflineQueue;
import com.lms.management.service.AttendanceOfflineQueueService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/attendance/offline-queue")
@RequiredArgsConstructor
public class AttendanceOfflineQueueController {

    private final AttendanceOfflineQueueService attendanceOfflineQueueService;

    // 1️⃣ STORE OFFLINE ATTENDANCE (MOBILE)
    @PostMapping
    @PreAuthorize("hasAuthority('ATTENDANCE_OFFLINE_QUEUE_CREATE')")
    public AttendanceOfflineQueue storeOfflineAttendance(
            @RequestBody AttendanceOfflineQueue queue
    ) {
        return attendanceOfflineQueueService.save(queue);
    }

    // 2️⃣ VIEW OFFLINE QUEUE BY BATCH
    @GetMapping("/batch/{batchId}")
    @PreAuthorize("hasAuthority('ATTENDANCE_OFFLINE_QUEUE_VIEW')")
    public List<AttendanceOfflineQueue> getByBatch(
            @PathVariable Long batchId
    ) {
        return attendanceOfflineQueueService.getByBatch(batchId);
    }

    // 3️⃣ SYNC OFFLINE → ATTENDANCE RECORD
    @PostMapping("/sync")
    @PreAuthorize("hasAuthority('ATTENDANCE_OFFLINE_SYNC')")
    public void syncOfflineQueue() {
        attendanceOfflineQueueService.sync();
    }

    // 4️⃣ DELETE OFFLINE QUEUE RECORD (ADMIN ONLY)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ATTENDANCE_OFFLINE_QUEUE_DELETE')")
    public void deleteQueue(
            @PathVariable Long id
    ) {
        attendanceOfflineQueueService.delete(id);
    }
}

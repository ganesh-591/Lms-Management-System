package com.lms.management.service;

import java.util.List;

import com.lms.management.model.AttendanceOfflineQueue;

public interface AttendanceOfflineQueueService {

    /**
     * Fetch all offline attendance records
     * where synced = false
     * Used for Sync Queue screen
     */
    List<AttendanceOfflineQueue> getPendingOfflineRecords();

    /**
     * Sync offline attendance records into attendance_record table
     *
     * @param syncedByUserId Admin / Trainer who triggered the sync
     */
    void syncOfflineAttendance(Long syncedByUserId);
}

package com.lms.management.service;

public interface EmailNotificationService {

    void sendAttendanceAlert(
            Long studentId,
            String flagType,
            int attendancePercent
    );

    // ✅ ADD THIS
    void sendManualAttendanceAlert(Long studentId, String flagType);
}

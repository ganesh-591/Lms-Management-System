package com.lms.management.service;

public interface EmailNotificationService {

    void sendAttendanceAlert(
            String toEmail,
            int attendancePercent,
            int consecutiveAbsentDays
    );
}
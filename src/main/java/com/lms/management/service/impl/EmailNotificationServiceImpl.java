package com.lms.management.service.impl;

import org.springframework.stereotype.Service;

import com.lms.management.service.EmailNotificationService;

@Service
public class EmailNotificationServiceImpl
        implements EmailNotificationService {

    @Override
    public void sendAttendanceAlert(
            Long studentId,
            String flagType,
            int attendancePercent
    ) {
        // real email integration later
        System.out.println(
            "AUTO ALERT → student=" + studentId +
            ", type=" + flagType +
            ", percent=" + attendancePercent
        );
    }

    // ✅ ADD THIS METHOD
    @Override
    public void sendManualAttendanceAlert(
            Long studentId,
            String flagType
    ) {
        System.out.println(
            "MANUAL ALERT → student=" + studentId +
            ", type=" + flagType
        );
    }
}

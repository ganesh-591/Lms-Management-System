package com.lms.management.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lms.management.service.EmailNotificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/attendance/alert")
@RequiredArgsConstructor
public class AttendanceAlertController {

    // ✅ INJECT SERVICE
    private final EmailNotificationService emailNotificationService;

    @PostMapping("/send")
    @PreAuthorize("hasAuthority('ATTENDANCE_ALERT_SEND')")
    public void sendManualAlert(
            @RequestParam Long studentId,
            @RequestParam String flagType
    ) {
        emailNotificationService
                .sendManualAttendanceAlert(studentId, flagType);
    }
}

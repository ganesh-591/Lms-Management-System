package com.lms.management.service.impl;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;


import com.lms.management.service.EmailNotificationService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EmailNotificationServiceImpl
        implements EmailNotificationService {

    private final JavaMailSender mailSender;

    @Override
    public void sendAttendanceAlert(
            String toEmail,
            int attendancePercent,
            int consecutiveAbsentDays
    ) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Attendance Warning");
        message.setText(
                "You have been absent for "
                + consecutiveAbsentDays
                + " consecutive days.\n"
                + "Your attendance is "
                + attendancePercent
                + "%.\n"
                + "Please attend classes regularly."
        );

        mailSender.send(message);
    }
}

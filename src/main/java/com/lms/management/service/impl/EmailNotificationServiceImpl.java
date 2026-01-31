package com.lms.management.service.impl;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.lms.management.service.EmailNotificationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailNotificationServiceImpl
        implements EmailNotificationService {

    private final JavaMailSender mailSender;

    @Override
    public void sendAttendanceAlert(
            Long studentId,
            String flagType,
            int attendancePercent
    ) {

        // ⚠ TEMP: static email (NO user module dependency)
        String toEmail = "student.alert@test.com";

        String subject = "Attendance Alert";
        String body =
                "Student ID: " + studentId + "\n" +
                "Your attendance is at risk.\n\n" +
                "Attendance Percentage: " + attendancePercent + "%\n" +
                "Status: AT_RISK\n\n" +
                "Please attend upcoming sessions.";

        try {
        	SimpleMailMessage message = new SimpleMailMessage();
        	message.setFrom("lmssender4@gmail.com");   // ✅ ADD THIS
        	message.setTo(toEmail);
        	message.setSubject("Attendance Alert");
        	message.setText(body);

            mailSender.send(message);

            System.out.println(
                "📧 Attendance alert email sent for studentId="
                + studentId
            );

        } catch (Exception e) {
            System.err.println(
                "❌ Failed to send attendance alert email: "
                + e.getMessage()
            );
        }
    }

    @Override
    public void sendManualAttendanceAlert(
            Long studentId,
            String flagType
    ) {
        // not used now
    }
}

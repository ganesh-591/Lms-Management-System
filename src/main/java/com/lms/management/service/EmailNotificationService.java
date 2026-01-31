package com.lms.management.service;

public interface EmailNotificationService {

   public void sendAttendanceAlert(
            Long studentId,
            String alertType,
            int attendancePercent
    );
   
   void sendManualAttendanceAlert(Long studentId, String flagType);
}
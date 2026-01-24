package com.lms.management.service;

import java.time.LocalDate;
import java.util.List;

import com.lms.management.model.AttendanceRecord;

public interface AttendanceRecordService {

    // ===============================
    // MARK ATTENDANCE (SINGLE)
    // ===============================
    AttendanceRecord markAttendance(AttendanceRecord record);

    // ===============================
    // MARK ATTENDANCE (BULK)
    // ===============================
    List<AttendanceRecord> markAttendanceBulk(List<AttendanceRecord> records);

    // ===============================
    // UPDATE ATTENDANCE (PATCH)
    // ===============================
    AttendanceRecord updateAttendance(
            Long attendanceRecordId,
            AttendanceRecord incoming
    );

    // ===============================
    // GET ALL RECORDS FOR A SESSION
    // ===============================
    List<AttendanceRecord> getByAttendanceSession(Long attendanceSessionId);

    // ===============================
    // GET RECORDS BY DATE (DASHBOARD / REPORTS)
    // ===============================
    List<AttendanceRecord> getByDate(LocalDate date);

    // ===============================
    // GET RECORDS BY SESSION + DATE
    // ===============================
    List<AttendanceRecord> getBySessionAndDate(
            Long attendanceSessionId,
            LocalDate date
    );

    // ===============================
    // STUDENT SELF / ADMIN VIEW
    // ===============================
    List<AttendanceRecord> getByStudent(Long studentId);

    // ===============================
    // DELETE ATTENDANCE RECORD
    // ===============================
    void delete(Long attendanceRecordId);
}

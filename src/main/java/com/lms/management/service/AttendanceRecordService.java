package com.lms.management.service;

import java.util.List;

import com.lms.management.model.AttendanceRecord;

public interface AttendanceRecordService {

    // ===============================
    // MARK ATTENDANCE (SINGLE)
    // ===============================
    AttendanceRecord markAttendance(AttendanceRecord record);

    // ===============================
    // ✅ MARK ATTENDANCE (BULK)
    // ===============================
    List<AttendanceRecord> markAttendanceBulk(List<AttendanceRecord> records);

    // ===============================
    // UPDATE ATTENDANCE (PUT = PATCH)
    // ===============================
    AttendanceRecord updateAttendance(Long attendanceRecordId, AttendanceRecord incoming);

    // ===============================
    // GET ALL RECORDS FOR A SESSION
    // ===============================
    List<AttendanceRecord> getByAttendanceSession(Long attendanceSessionId);

    // ===============================
    // STUDENT SELF VIEW
    // ===============================
    List<AttendanceRecord> getByStudent(Long studentId);

    // ===============================
    // DELETE ATTENDANCE RECORD
    // ===============================
    void delete(Long attendanceRecordId);
}

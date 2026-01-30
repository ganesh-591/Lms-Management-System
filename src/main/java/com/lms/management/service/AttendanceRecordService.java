package com.lms.management.service;

import java.time.LocalDate;
import java.util.List;

import com.lms.management.model.AttendanceRecord;
import com.lms.management.model.StudentAttendanceStatus;

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
    
    void markLeave(Long attendanceSessionId, Long studentId);
    
    boolean isStudentAtRisk(Long studentId, Long courseId, Long batchId);

    boolean isStudentEligible(Long studentId, Long courseId, Long batchId);

    int getAttendancePercentage(Long studentId, Long courseId, Long batchId);
    
    List<StudentAttendanceStatus> getDashboardAttendanceStatus(
    	    Long courseId,
    	    Long batchId
    	);
    
    boolean isStudentAtRiskByAbsence(
            Long studentId,
            Long courseId,
            Long batchId
    );
    
}

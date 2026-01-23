package com.lms.management.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.management.exception.ResourceNotFoundException;
import com.lms.management.model.AttendanceRecord;
import com.lms.management.model.AttendanceSession;
import com.lms.management.repository.AttendanceRecordRepository;
import com.lms.management.repository.AttendanceSessionRepository;
import com.lms.management.repository.StudentBatchRepository;
import com.lms.management.service.AttendanceRecordService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AttendanceRecordServiceImpl implements AttendanceRecordService {

    private final AttendanceRecordRepository attendanceRecordRepository;
    private final AttendanceSessionRepository attendanceSessionRepository;
    private final StudentBatchRepository studentBatchRepository;

    // ===============================
    // MARK ATTENDANCE (SINGLE)
    // ===============================
    @Override
    public AttendanceRecord markAttendance(AttendanceRecord record) {

        validateAttendance(record);

        return attendanceRecordRepository.save(record);
    }

    // ===============================
    // ✅ MARK ATTENDANCE (BULK)
    // ===============================
    @Override
    public List<AttendanceRecord> markAttendanceBulk(
            List<AttendanceRecord> records
    ) {

        List<AttendanceRecord> savedRecords = new ArrayList<>();

        for (AttendanceRecord record : records) {
            validateAttendance(record);
            savedRecords.add(attendanceRecordRepository.save(record));
        }

        return savedRecords;
    }

    // ===============================
    // COMMON VALIDATION
    // ===============================
    private void validateAttendance(AttendanceRecord record) {

        // 1️⃣ Check attendance session exists
        AttendanceSession session =
                attendanceSessionRepository.findById(
                        record.getAttendanceSessionId()
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Attendance session not found"
                        )
                );

        // 2️⃣ Validate student belongs to batch
        boolean isEnrolled =
                studentBatchRepository.existsByStudentIdAndBatchIdAndStatus(
                        record.getStudentId(),
                        session.getBatchId(),
                        "ACTIVE"
                );

        if (!isEnrolled) {
            throw new IllegalStateException(
                    "Student is not enrolled in this batch"
            );
        }

        // 3️⃣ Prevent duplicate attendance
        attendanceRecordRepository
                .findByAttendanceSessionIdAndStudentId(
                        record.getAttendanceSessionId(),
                        record.getStudentId()
                )
                .ifPresent(r -> {
                    throw new IllegalStateException(
                            "Attendance already marked for this student"
                    );
                });
    }

    // ===============================
    // UPDATE ATTENDANCE
    // ===============================
    @Override
    public AttendanceRecord updateAttendance(
            Long attendanceRecordId,
            AttendanceRecord incoming
    ) {

        AttendanceRecord existing =
                attendanceRecordRepository.findById(attendanceRecordId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Attendance record not found"
                                )
                        );

        if (incoming.getStatus() != null)
            existing.setStatus(incoming.getStatus());

        if (incoming.getRemarks() != null)
            existing.setRemarks(incoming.getRemarks());

        return attendanceRecordRepository.save(existing);
    }

    // ===============================
    // GET BY ATTENDANCE SESSION
    // ===============================
    @Override
    @Transactional(readOnly = true)
    public List<AttendanceRecord> getByAttendanceSession(
            Long attendanceSessionId
    ) {

        return attendanceRecordRepository
                .findByAttendanceSessionId(attendanceSessionId);
    }

    // ===============================
    // STUDENT SELF VIEW
    // ===============================
    @Override
    @Transactional(readOnly = true)
    public List<AttendanceRecord> getByStudent(Long studentId) {

        return attendanceRecordRepository.findByStudentId(studentId);
    }

    // ===============================
    // DELETE ATTENDANCE RECORD
    // ===============================
    @Override
    public void delete(Long attendanceRecordId) {

        AttendanceRecord record =
                attendanceRecordRepository.findById(attendanceRecordId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Attendance record not found"
                                )
                        );

        attendanceRecordRepository.delete(record);
    }
}

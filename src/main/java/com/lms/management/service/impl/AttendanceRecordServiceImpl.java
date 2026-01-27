package com.lms.management.service.impl;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.management.exception.ResourceNotFoundException;
import com.lms.management.model.AttendanceConfig;
import com.lms.management.model.AttendanceRecord;
import com.lms.management.model.AttendanceSession;
import com.lms.management.repository.AttendanceConfigRepository;
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
    private final AttendanceConfigRepository attendanceConfigRepository;

    // ===============================
    // MARK ATTENDANCE (SINGLE)
    // ===============================
    @Override
    public AttendanceRecord markAttendance(AttendanceRecord record) {

        AttendanceSession session = validateAttendance(record);
        AttendanceConfig config = getConfig(session.getBatchId());

        // backend decides status
        record.setStatus(resolveStatus(session, record, config));

        return attendanceRecordRepository.save(record);
    }

    // ===============================
    // MARK ATTENDANCE (BULK)
    // ===============================
    @Override
    public List<AttendanceRecord> markAttendanceBulk(List<AttendanceRecord> records) {

        List<AttendanceRecord> saved = new ArrayList<>();

        for (AttendanceRecord record : records) {

            AttendanceSession session = validateAttendance(record);
            AttendanceConfig config = getConfig(session.getBatchId());

            record.setStatus(resolveStatus(session, record, config));
            saved.add(attendanceRecordRepository.save(record));
        }

        return saved;
    }

    // ===============================
    // COMMON VALIDATION
    // ===============================
    private AttendanceSession validateAttendance(AttendanceRecord record) {

        AttendanceSession session =
                attendanceSessionRepository.findById(
                        record.getAttendanceSessionId()
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Attendance session not found"
                        )
                );

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

        if (record.getMarkedAt() == null) {
            record.setMarkedAt(LocalDateTime.now());
        }

        record.setAttendanceDate(session.getStartedAt().toLocalDate());

        return session;
    }

    // ===============================
    // LOAD CONFIG
    // ===============================
    private AttendanceConfig getConfig(Long batchId) {
        return attendanceConfigRepository.findByBatchId(batchId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Attendance config not found for batch"
                        )
                );
    }

    // ===============================
    // STATUS DECISION ENGINE 🔥
    // ===============================
    private String resolveStatus(
            AttendanceSession session,
            AttendanceRecord record,
            AttendanceConfig config
    ) {

        LocalDateTime sessionStart = session.getStartedAt();
        LocalDateTime markedAt = record.getMarkedAt();

        long minutesLate =
                Duration.between(sessionStart, markedAt).toMinutes();

        // 1️⃣ Grace Time
        if (Boolean.TRUE.equals(config.getAllowGraceTime())
                && minutesLate <= config.getGraceTimeMinutes()) {
            return "PRESENT";
        }

        // 2️⃣ Late
        if (Boolean.TRUE.equals(config.getAllowLate())
                && minutesLate <= config.getLateAfterMinutes()) {
            return "LATE";
        }

        // 3️⃣ Partial
        if (Boolean.TRUE.equals(config.getAllowPartialAttendance())) {
            return "PARTIAL";
        }

        // 4️⃣ Absent
        if (Boolean.TRUE.equals(config.getRequireRemarksForAbsent())
                && record.getRemarks() == null) {
            throw new IllegalStateException(
                    "Remarks required for absent attendance"
            );
        }

        return "ABSENT";
    }

    // ===============================
    // UPDATE ATTENDANCE (ADMIN ONLY)
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

        if (incoming.getRemarks() != null) {
            existing.setRemarks(incoming.getRemarks());
        }

        // status override only if admin
        if (incoming.getStatus() != null) {
            existing.setStatus(incoming.getStatus());
        }

        return attendanceRecordRepository.save(existing);
    }

    // ===============================
    // GET APIs
    // ===============================
    @Override
    @Transactional(readOnly = true)
    public List<AttendanceRecord> getByAttendanceSession(Long sessionId) {
        return attendanceRecordRepository.findByAttendanceSessionId(sessionId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceRecord> getByDate(LocalDate date) {
        return attendanceRecordRepository.findByAttendanceDate(date);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceRecord> getBySessionAndDate(
            Long sessionId,
            LocalDate date
    ) {
        return attendanceRecordRepository
                .findByAttendanceSessionIdAndAttendanceDate(sessionId, date);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceRecord> getByStudent(Long studentId) {
        return attendanceRecordRepository.findByStudentId(studentId);
    }

    // ===============================
    // DELETE
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

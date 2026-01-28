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
    private final AttendanceConfigRepository attendanceConfigRepository;
    private final StudentBatchRepository studentBatchRepository;

    @Override
    public AttendanceRecord markAttendance(AttendanceRecord record) {

        AttendanceSession session = validateAttendance(record);
        AttendanceConfig config =
                getConfig(session.getCourseId(), session.getBatchId());

        applyDefaults(record, session);
        applyTimeBasedStatus(record, session, config);
        validateEarlyExitRule(config, record);

        return attendanceRecordRepository.save(record);
    }

    @Override
    public List<AttendanceRecord> markAttendanceBulk(List<AttendanceRecord> records) {

        List<AttendanceRecord> saved = new ArrayList<>();

        for (AttendanceRecord record : records) {
            AttendanceSession session = validateAttendance(record);
            AttendanceConfig config =
                    getConfig(session.getCourseId(), session.getBatchId());

            applyDefaults(record, session);
            applyTimeBasedStatus(record, session, config);
            validateEarlyExitRule(config, record);

            saved.add(attendanceRecordRepository.save(record));
        }

        return saved;
    }

    private AttendanceSession validateAttendance(AttendanceRecord record) {

        AttendanceSession session =
                attendanceSessionRepository.findById(
                        record.getAttendanceSessionId()
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Attendance session not found"
                        )
                );

        boolean enrolled =
                studentBatchRepository.existsByStudentIdAndBatchIdAndStatus(
                        record.getStudentId(),
                        session.getBatchId(),
                        "ACTIVE"
                );

        if (!enrolled) {
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

        return session;
    }

    // ===============================
    // TIME-BASED STATUS CALCULATION
    // ===============================
    private void applyTimeBasedStatus(
            AttendanceRecord record,
            AttendanceSession session,
            AttendanceConfig config
    ) {

        LocalDateTime joinTime = record.getMarkedAt();
        LocalDateTime sessionStart = session.getStartedAt();

        long delayMinutes =
                Duration.between(sessionStart, joinTime).toMinutes();

        if (delayMinutes <= config.getGracePeriodMinutes()) {
            record.setStatus("PRESENT");
            return;
        }

        if (delayMinutes <= config.getLateGraceMinutes()) {
            record.setStatus("LATE");
            return;
        }

        record.setStatus("ABSENT");
    }

    // ===============================
    // EARLY EXIT (PARTIAL)
    // ===============================
    private void validateEarlyExitRule(
            AttendanceConfig config,
            AttendanceRecord record
    ) {

        if ("PARTIAL".equals(record.getStatus())) {

            if (!"MARK_PARTIAL".equalsIgnoreCase(
                    config.getEarlyExitAction())) {

                throw new IllegalStateException(
                        "Partial attendance is not allowed for this batch"
                );
            }
        }
    }

    private void applyDefaults(
            AttendanceRecord record,
            AttendanceSession session
    ) {

        if (record.getMarkedAt() == null) {
            record.setMarkedAt(LocalDateTime.now());
        }

        if (record.getAttendanceDate() == null) {
            record.setAttendanceDate(
                    session.getStartedAt().toLocalDate()
            );
        }

        if (record.getSource() == null) {
            record.setSource("MANUAL");
        }
    }

    private AttendanceConfig getConfig(
            Long courseId,
            Long batchId
    ) {
        return attendanceConfigRepository
                .findByCourseIdAndBatchId(courseId, batchId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Attendance config not found for course & batch"
                        )
                );
    }

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

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceRecord> getByAttendanceSession(Long attendanceSessionId) {
        return attendanceRecordRepository
                .findByAttendanceSessionId(attendanceSessionId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceRecord> getByDate(LocalDate date) {
        return attendanceRecordRepository.findByAttendanceDate(date);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceRecord> getBySessionAndDate(
            Long attendanceSessionId,
            LocalDate date
    ) {
        return attendanceRecordRepository
                .findByAttendanceSessionIdAndAttendanceDate(
                        attendanceSessionId,
                        date
                );
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceRecord> getByStudent(Long studentId) {
        return attendanceRecordRepository.findByStudentId(studentId);
    }

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

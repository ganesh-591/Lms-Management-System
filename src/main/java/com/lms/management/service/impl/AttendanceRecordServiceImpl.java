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

    // ===============================
    // JOIN (MARK ATTENDANCE)
    // ===============================
    @Override
    public AttendanceRecord markAttendance(AttendanceRecord record) {

        AttendanceSession session = validateAttendance(record);
        validateSessionNotEnded(session);

        applyDefaults(record, session);

        // Status is temporary – finalized on LEAVE
        record.setStatus("PRESENT");

        return attendanceRecordRepository.save(record);
    }

    @Override
    public List<AttendanceRecord> markAttendanceBulk(List<AttendanceRecord> records) {

        List<AttendanceRecord> saved = new ArrayList<>();

        for (AttendanceRecord record : records) {
            AttendanceSession session = validateAttendance(record);
            validateSessionNotEnded(session);

            applyDefaults(record, session);
            record.setStatus("PRESENT");

            saved.add(attendanceRecordRepository.save(record));
        }

        return saved;
    }

    // ===============================
    // LEAVE (FINALIZE ATTENDANCE)
    // ===============================
    @Override
    public void markLeave(Long attendanceSessionId, Long studentId) {

        AttendanceRecord record =
                attendanceRecordRepository
                        .findByAttendanceSessionIdAndStudentId(
                                attendanceSessionId,
                                studentId
                        )
                        .orElseThrow(() ->
                                new IllegalStateException("Attendance not found"));

        AttendanceSession session =
                attendanceSessionRepository.findById(attendanceSessionId)
                        .orElseThrow(() ->
                                new IllegalStateException("Session not found"));

        AttendanceConfig config =
                getConfig(session.getCourseId(), session.getBatchId());

        // Prevent duplicate leave
        if ("ABSENT".equals(record.getStatus())
                || "PARTIAL".equals(record.getStatus())) {
            return;
        }

        LocalDateTime leaveTime =
                session.getEndedAt() != null
                        ? session.getEndedAt()
                        : LocalDateTime.now();

        long sessionMinutes =
                Duration.between(
                        session.getStartedAt(),
                        leaveTime
                ).toMinutes();

        long attendedMinutes =
                Duration.between(
                        record.getMarkedAt(),
                        leaveTime
                ).toMinutes();

        if (sessionMinutes <= 0) {
            throw new IllegalStateException("Invalid session duration");
        }

        int attendancePercent =
                (int) ((attendedMinutes * 100) / sessionMinutes);

        // ===============================
        // FINAL STATUS DECISION
        // ===============================
        if (attendancePercent >= config.getMinPresenceMinutes()) {
            record.setStatus("PRESENT");
        } else if ("MARK_PARTIAL".equalsIgnoreCase(
                config.getEarlyExitAction())) {
            record.setStatus("PARTIAL");
        } else {
            record.setStatus("ABSENT");
        }

        attendanceRecordRepository.save(record);
    }

    // ===============================
    // VALIDATIONS
    // ===============================
    private AttendanceSession validateAttendance(AttendanceRecord record) {

        AttendanceSession session =
                attendanceSessionRepository
                        .findById(record.getAttendanceSessionId())
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Attendance session not found"));

        boolean enrolled =
                studentBatchRepository
                        .existsByStudentIdAndBatchIdAndStatus(
                                record.getStudentId(),
                                session.getBatchId(),
                                "ACTIVE"
                        );

        if (!enrolled) {
            throw new IllegalStateException(
                    "Student is not enrolled in this batch");
        }

        attendanceRecordRepository
                .findByAttendanceSessionIdAndStudentId(
                        record.getAttendanceSessionId(),
                        record.getStudentId()
                )
                .ifPresent(r -> {
                    throw new IllegalStateException(
                            "Attendance already marked");
                });

        return session;
    }

    private void validateSessionNotEnded(AttendanceSession session) {

        if ("ENDED".equalsIgnoreCase(session.getStatus())) {
            throw new IllegalStateException(
                    "Attendance session already ended");
        }

        if (session.getEndedAt() != null
                && LocalDateTime.now().isAfter(session.getEndedAt())) {
            throw new IllegalStateException(
                    "Attendance time window closed");
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
                    session.getStartedAt().toLocalDate());
        }

        if (record.getSource() == null) {
            record.setSource("ONLINE");
        }

        if (record.getMarkedBy() == null) {
            record.setMarkedBy(1L); // TEMP (JWT later)
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
                                "Attendance config not found"));
    }

    // ===============================
    // READ / UPDATE / DELETE
    // ===============================
    @Override
    public AttendanceRecord updateAttendance(
            Long id,
            AttendanceRecord incoming
    ) {

        AttendanceRecord existing =
                attendanceRecordRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Attendance record not found"));

        if (incoming.getStatus() != null)
            existing.setStatus(incoming.getStatus());

        if (incoming.getRemarks() != null)
            existing.setRemarks(incoming.getRemarks());

        return attendanceRecordRepository.save(existing);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceRecord> getByAttendanceSession(Long sessionId) {
        return attendanceRecordRepository
                .findByAttendanceSessionId(sessionId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceRecord> getByDate(LocalDate date) {
        return attendanceRecordRepository
                .findByAttendanceDate(date);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceRecord> getBySessionAndDate(
            Long sessionId,
            LocalDate date
    ) {
        return attendanceRecordRepository
                .findByAttendanceSessionIdAndAttendanceDate(
                        sessionId,
                        date);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceRecord> getByStudent(Long studentId) {
        return attendanceRecordRepository
                .findByStudentId(studentId);
    }

    @Override
    public void delete(Long id) {

        AttendanceRecord record =
                attendanceRecordRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Attendance record not found"));

        attendanceRecordRepository.delete(record);
    }
    
 // ===============================
 // AT-RISK CHECK (READ ONLY)
 // ===============================
 @Transactional(readOnly = true)
 public boolean isStudentAtRisk(Long studentId, Long courseId, Long batchId) {

     AttendanceConfig config =
             attendanceConfigRepository
                     .findByCourseIdAndBatchId(courseId, batchId)
                     .orElseThrow(() ->
                             new IllegalStateException("Attendance config not found"));

     int limit = config.getConsecutiveAbsenceLimit();

     List<AttendanceRecord> records =
             attendanceRecordRepository
                     .findTopByStudentIdOrderByAttendanceDateDesc(studentId, limit);

     int consecutiveAbsent = 0;

     for (AttendanceRecord record : records) {
         if ("ABSENT".equals(record.getStatus())) {
             consecutiveAbsent++;
         } else {
             break; // streak broken
         }
     }

     return consecutiveAbsent >= limit;
 }
}

package com.lms.management.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.lms.management.exception.ResourceNotFoundException;
import com.lms.management.model.AttendanceRecord;
import com.lms.management.model.AttendanceSession;
import com.lms.management.model.Session;
import com.lms.management.repository.AttendanceRecordRepository;
import com.lms.management.repository.AttendanceSessionRepository;
import com.lms.management.repository.SessionRepository;
import com.lms.management.repository.StudentBatchRepository;
import com.lms.management.service.AttendanceSessionService;
import com.lms.management.service.AttendanceSummaryService;
import com.lms.management.service.EmailNotificationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AttendanceSessionServiceImpl implements AttendanceSessionService {

    private final AttendanceSessionRepository attendanceSessionRepository;
    private final SessionRepository sessionRepository;

    // 🔹 ADDED (dependency only)
    private final AttendanceRecordRepository attendanceRecordRepository;
    private final StudentBatchRepository studentBatchRepository;
    private final AttendanceSummaryService attendanceSummaryService;
    private final EmailNotificationService emailNotificationService;
    
    // ===============================
    // START ATTENDANCE
    // ===============================
    @Override
    public AttendanceSession startAttendance(
            Long sessionId,
            Long courseId,
            Long batchId,
            Long userId
    ) {

        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Session not found")
                );

        if (!session.getBatchId().equals(batchId)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Session does not belong to this batch"
            );
        }

        attendanceSessionRepository
                .findBySessionIdAndStatus(sessionId, "ACTIVE")
                .ifPresent(a -> {
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "Attendance already started for this session"
                    );
                });

        AttendanceSession attendanceSession = new AttendanceSession();
        attendanceSession.setSessionId(sessionId);
        attendanceSession.setCourseId(courseId);
        attendanceSession.setBatchId(batchId);
        attendanceSession.setCreatedBy(userId);
        attendanceSession.setStartedAt(LocalDateTime.now());
        attendanceSession.setStatus("ACTIVE");

        return attendanceSessionRepository.save(attendanceSession);
    }

    // ===============================
    // END ATTENDANCE
    // ===============================
    @Override
    public AttendanceSession endAttendance(Long attendanceSessionId) {

        AttendanceSession attendanceSession =
                attendanceSessionRepository.findById(attendanceSessionId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Attendance session not found"
                                )
                        );

        attendanceSession.setStatus("ENDED");
        attendanceSession.setEndedAt(LocalDateTime.now());

        AttendanceSession saved =
                attendanceSessionRepository.save(attendanceSession);

        // 🔹 ADDED (auto-mark absent students)
        finalizeAbsentStudents(saved);
        
        checkAndTriggerAtRiskAlerts(saved);

        return saved;
    }
    
  

    // ===============================
    // 🔹 AUTO ABSENT LOGIC (ADDED)
    // ===============================
    private void finalizeAbsentStudents(AttendanceSession session) {

        Long batchId = session.getBatchId();
        Long attendanceSessionId = session.getId();

        // 1. All students in batch
        List<Long> allStudents =
                studentBatchRepository.findStudentIdsByBatchId(batchId);

        // 2. Students who already have attendance
        List<Long> markedStudents =
                attendanceRecordRepository
                        .findStudentIdsByAttendanceSessionId(
                                attendanceSessionId
                        );

        // 3. Mark remaining as ABSENT
        for (Long studentId : allStudents) {

            if (!markedStudents.contains(studentId)) {

                AttendanceRecord record = new AttendanceRecord();
                record.setAttendanceSessionId(attendanceSessionId);
                record.setStudentId(studentId);
                record.setAttendanceDate(LocalDate.now());
                record.setStatus("ABSENT");
                record.setSource("SYSTEM");
                record.setRemarks("Did not join session");
                record.setMarkedAt(session.getEndedAt());
                record.setMarkedBy(session.getCreatedBy());

                attendanceRecordRepository.save(record);
            }
        }
    }
        
    // ===============================
    // GET BY ID
    // ===============================
    @Override
    @Transactional(readOnly = true)
    public AttendanceSession getById(Long attendanceSessionId) {

        return attendanceSessionRepository.findById(attendanceSessionId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Attendance session not found"
                        )
                );
    }

    // ===============================
    // GET ACTIVE BY SESSION ID
    // ===============================
    @Override
    @Transactional(readOnly = true)
    public AttendanceSession getActiveBySessionId(Long sessionId) {

        return attendanceSessionRepository
                .findBySessionIdAndStatus(sessionId, "ACTIVE")
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No active attendance for this session"
                        )
                );
    }

    // ===============================
    // GET ACTIVE + ENDED
    // ===============================
    @Override
    @Transactional(readOnly = true)
    public List<AttendanceSession> getActiveAndEndedBySessionId(Long sessionId) {

        return attendanceSessionRepository.findAll().stream()
                .filter(a ->
                        a.getSessionId().equals(sessionId) &&
                        (a.getStatus().equals("ACTIVE")
                                || a.getStatus().equals("ENDED"))
                )
                .toList();
    }

    // ===============================
    // GET BY DATE
    // ===============================
    @Override
    @Transactional(readOnly = true)
    public List<AttendanceSession> getByDate(LocalDate date) {

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(LocalTime.MAX);

        return attendanceSessionRepository
                .findByStartedAtBetween(start, end);
    }

    // ===============================
    // DELETE
    // ===============================
    @Override
    public void delete(Long attendanceSessionId) {

        AttendanceSession session =
                attendanceSessionRepository.findById(attendanceSessionId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Attendance session not found"
                                )
                        );

        attendanceSessionRepository.delete(session);
    }
    
    private void checkAndTriggerAtRiskAlerts(AttendanceSession session) {

        Long courseId = session.getCourseId();
        Long batchId = session.getBatchId();

        // get all students of the batch
        List<Long> studentIds =
                studentBatchRepository.findStudentIdsByBatchId(batchId);

        for (Long studentId : studentIds) {

            Map<String, Object> summary =
                    attendanceSummaryService.getStudentEligibilitySummary(
                            studentId,
                            courseId,
                            batchId
                    );

            Boolean atRisk = (Boolean) summary.get("atRisk");

            if (Boolean.TRUE.equals(atRisk)) {

                System.out.println(
                    "AT-RISK student detected -> studentId=" + studentId
                );

                emailNotificationService.sendAttendanceAlert(
                    studentId,
                    "AT_RISK",
                    (Integer) summary.get("attendancePercent")
                );
            }
        }
    }
    
}

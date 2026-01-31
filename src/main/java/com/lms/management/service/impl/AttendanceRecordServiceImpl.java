package com.lms.management.service.impl;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.management.exception.ResourceNotFoundException;
import com.lms.management.model.AttendanceAlertFlag;
import com.lms.management.model.AttendanceConfig;
import com.lms.management.model.AttendanceRecord;
import com.lms.management.model.AttendanceSession;
import com.lms.management.model.StudentAttendanceStatus;
import com.lms.management.repository.AttendanceAlertFlagRepository;
import com.lms.management.repository.AttendanceConfigRepository;
import com.lms.management.repository.AttendanceRecordRepository;
import com.lms.management.repository.AttendanceSessionRepository;
import com.lms.management.repository.StudentBatchRepository;
import com.lms.management.service.AttendanceRecordService;
import com.lms.management.service.EmailNotificationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AttendanceRecordServiceImpl implements AttendanceRecordService {

    private final AttendanceRecordRepository attendanceRecordRepository;
    private final AttendanceSessionRepository attendanceSessionRepository;
    private final AttendanceConfigRepository attendanceConfigRepository;
    private final StudentBatchRepository studentBatchRepository;
    private final AttendanceAlertFlagRepository attendanceAlertFlagRepository;
    private final EmailNotificationService emailNotificationService;
    

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
    @Transactional
    public void markLeave(Long attendanceSessionId, Long studentId) {

        AttendanceRecord record =
                attendanceRecordRepository
                        .findByAttendanceSessionIdAndStudentId(
                                attendanceSessionId, studentId
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

        LocalDateTime leaveTime = LocalDateTime.now();
        record.setLeftAt(leaveTime);

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

        // ✅ APPLY STATUS
        if (attendedMinutes < config.getMinPresenceMinutes()) {

            record.setStatus("ABSENT");
            record.setRemarks("Left before minimum presence time");

        } else if (attendancePercent < config.getExamEligibilityPercent()) {

            record.setStatus("PARTIAL");
            record.setRemarks("Partial attendance");

        } else {

            record.setStatus("PRESENT");
            record.setRemarks("Attended and left");
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
    public boolean isStudentAtRiskByAbsence(
            Long studentId,
            Long courseId,
            Long batchId
    ) {

        AttendanceConfig config =
                attendanceConfigRepository
                        .findByCourseIdAndBatchId(courseId, batchId)
                        .orElseThrow(() ->
                                new IllegalStateException("Attendance config not found")
                        );

        int limit = config.getConsecutiveAbsenceLimit();

        // ✅ CORRECT: use Pageable
        List<AttendanceRecord> records =
                attendanceRecordRepository
                        .findByStudentIdOrderByAttendanceDateDesc(
                                studentId,
                                PageRequest.of(0, limit)
                        );

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
 
 @Transactional(readOnly = true)
 public int calculateAttendancePercentage(
         Long studentId,
         Long courseId,
         Long batchId
 ) {

     // 1. Get all sessions for course + batch
     List<AttendanceSession> sessions =
             attendanceSessionRepository
                     .findByCourseIdAndBatchId(courseId, batchId);

     if (sessions.isEmpty()) return 0;

     List<Long> sessionIds =
             sessions.stream()
                     .map(AttendanceSession::getId)
                     .toList();

     // 2. Total sessions
     long totalSessions = sessionIds.size();

     // 3. Attended sessions (PRESENT + PARTIAL)
     long attendedSessions =
             attendanceRecordRepository
                     .countByStudentIdAndAttendanceSessionIdInAndStatusIn(
                             studentId,
                             sessionIds,
                             List.of("PRESENT", "PARTIAL")
                     );

     // 4. Percentage
     return (int) ((attendedSessions * 100) / totalSessions);
 }
 
 @Transactional(readOnly = true)
 public int getAttendancePercentage(Long studentId, Long courseId, Long batchId) {

     // 1. Get all sessions
     List<AttendanceSession> sessions =
             attendanceSessionRepository.findByCourseIdAndBatchId(courseId, batchId);

     if (sessions.isEmpty()) return 0;

     List<Long> sessionIds =
             sessions.stream()
                     .map(AttendanceSession::getId)
                     .toList();

     // 2. Total sessions
     long totalSessions = sessionIds.size();

     // 3. Attended sessions (PRESENT + PARTIAL)
     long attendedSessions =
             attendanceRecordRepository
                     .countByStudentIdAndAttendanceSessionIdInAndStatusIn(
                             studentId,
                             sessionIds,
                             List.of("PRESENT", "PARTIAL")
                     );

     // 4. Percentage
     return (int) ((attendedSessions * 100) / totalSessions);
 }
 
 @Override
 public boolean isStudentEligible(Long studentId, Long courseId, Long batchId) {
     AttendanceConfig config =
             attendanceConfigRepository
                     .findByCourseIdAndBatchId(courseId, batchId)
                     .orElseThrow(() ->
                             new IllegalStateException("Attendance config not found"));

     int attendancePercent =
             getAttendancePercentage(studentId, courseId, batchId);

     return attendancePercent >= config.getExamEligibilityPercent();
 }
 
 
 @Override
 public boolean isStudentAtRisk(Long studentId, Long courseId, Long batchId) {
     AttendanceConfig config =
             attendanceConfigRepository
                     .findByCourseIdAndBatchId(courseId, batchId)
                     .orElseThrow(() ->
                             new IllegalStateException("Attendance config not found"));

     int attendancePercent =
             getAttendancePercentage(studentId, courseId, batchId);

     return attendancePercent < config.getAtRiskPercent();
 }
 
 @Override
 @Transactional(readOnly = true)
 public List<StudentAttendanceStatus> getDashboardAttendanceStatus(
         Long courseId,
         Long batchId
         
 ) {

     // 1. Get all students in batch
     List<Long> studentIds =
             studentBatchRepository.findStudentIdsByBatchId(batchId);

     List<StudentAttendanceStatus> result = new ArrayList<>();

     for (Long studentId : studentIds) {

         int percent =
                 getAttendancePercentage(studentId, courseId, batchId);

         boolean eligible =
                 isStudentEligible(studentId, courseId, batchId);

         boolean atRiskByPercent =
                 percent < getConfig(courseId, batchId).getAtRiskPercent();

         boolean atRiskByAbsence =
                 isStudentAtRiskByAbsence(studentId, courseId, batchId);

         StudentAttendanceStatus status = new StudentAttendanceStatus();

         status.setStudentId(studentId);
         status.setAttendancePercent(percent);
         status.setEligible(eligible);

         syncAndNotify(
        	        studentId,
        	        courseId,
        	        batchId,
        	        "AT_RISK_PERCENT",
        	        atRiskByPercent,
        	        percent
        	);

        	syncAndNotify(
        	        studentId,
        	        courseId,
        	        batchId,
        	        "CONSECUTIVE_ABSENCE",
        	        atRiskByAbsence,
        	        percent
        	);

        	syncAndNotify(
        	        studentId,
        	        courseId,
        	        batchId,
        	        "NOT_ELIGIBLE",
        	        !eligible,
        	        percent
        	);
         // keep risks separate
         status.setAtRiskByPercent(atRiskByPercent);
         status.setAtRiskByAbsence(atRiskByAbsence);

         // alert flag (ONLY indicates alert is needed)
         status.setAlertRequired(atRiskByPercent || atRiskByAbsence);

         // alert reason (single dominant reason, no mixing)
         if (atRiskByAbsence) {
             status.setAlertReason("CONSECUTIVE_ABSENCE");
         } else if (atRiskByPercent) {
             status.setAlertReason("LOW_ATTENDANCE_PERCENT");
         } else {
             status.setAlertReason(null);
         }

         result.add(status);
     }

     return result;
 }
 
 @Transactional(readOnly = true)
 public String getStudentAttendanceFlag(
         Long studentId,
         Long courseId,
         Long batchId
 ) {

     // 1️⃣ Check consecutive absence (HIGH PRIORITY)
     boolean atRiskByAbsence =
             isStudentAtRiskByAbsence(studentId, courseId, batchId);

     if (atRiskByAbsence) {
         return "AT_RISK_ABSENCE";
     }

     // 2️⃣ Calculate attendance percentage
     int attendancePercent =
             getAttendancePercentage(studentId, courseId, batchId);

     AttendanceConfig config =
             attendanceConfigRepository
                     .findByCourseIdAndBatchId(courseId, batchId)
                     .orElseThrow(() ->
                             new IllegalStateException("Attendance config not found"));

     // 3️⃣ Eligibility check
     if (attendancePercent < config.getExamEligibilityPercent()) {
         return "NOT_ELIGIBLE";
     }

     // 4️⃣ At-risk percentage check
     if (attendancePercent < config.getAtRiskPercent()) {
         return "AT_RISK_PERCENTAGE";
     }

     // 5️⃣ Safe
     return "OK";
 }
 
 private void syncAlertFlag(
	        Long studentId,
	        Long courseId,
	        Long batchId,
	        String flagType,
	        boolean conditionActive
	) {

	    attendanceAlertFlagRepository
	            .findByStudentIdAndCourseIdAndBatchIdAndFlagType(
	                    studentId, courseId, batchId, flagType
	            )
	            .ifPresentOrElse(flag -> {

	                // CONDITION RESOLVED → close flag
	                if (!conditionActive && "ACTIVE".equals(flag.getStatus())) {
	                    flag.setStatus("RESOLVED");
	                    flag.setResolvedAt(LocalDateTime.now());
	                    attendanceAlertFlagRepository.save(flag);
	                }

	            }, () -> {

	                // NEW RISK → create flag + SEND EMAIL ONCE
	                if (conditionActive) {

	                    AttendanceAlertFlag flag = new AttendanceAlertFlag();
	                    flag.setStudentId(studentId);
	                    flag.setCourseId(courseId);
	                    flag.setBatchId(batchId);
	                    flag.setFlagType(flagType);

	                    attendanceAlertFlagRepository.save(flag);

	                    // 🔔 EXACT LINE YOU ADD
	                    emailNotificationService.sendAttendanceAlert(
	                            studentId,
	                            flagType,
	                            getAttendancePercentage(studentId, courseId, batchId)
	                    );
	                }
	            });
	}
 
 private void syncAndNotify(
	        Long studentId,
	        Long courseId,
	        Long batchId,
	        String flagType,
	        boolean conditionActive,
	        int attendancePercent
	) {

	    attendanceAlertFlagRepository
	        .findByStudentIdAndCourseIdAndBatchIdAndFlagType(
	                studentId, courseId, batchId, flagType
	        )
	        .ifPresentOrElse(flag -> {

	            // condition cleared → resolve flag
	            if (!conditionActive && "ACTIVE".equals(flag.getStatus())) {
	                flag.setStatus("RESOLVED");
	                flag.setResolvedAt(LocalDateTime.now());
	                attendanceAlertFlagRepository.save(flag);
	            }

	        }, () -> {

	            // NEW risk → create flag + SEND AUTO EMAIL
	            if (conditionActive) {
	                AttendanceAlertFlag flag = new AttendanceAlertFlag();
	                flag.setStudentId(studentId);
	                flag.setCourseId(courseId);
	                flag.setBatchId(batchId);
	                flag.setFlagType(flagType);
	                attendanceAlertFlagRepository.save(flag);

	                // 🔔 AUTO EMAIL (ONCE)
	                emailNotificationService.sendAttendanceAlert(
	                        studentId,
	                        flagType,
	                        attendancePercent
	                );
	            }
	        });
	}

}

package com.lms.management.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.management.model.AttendanceConfig;
import com.lms.management.model.AttendanceRecord;
import com.lms.management.repository.AttendanceConfigRepository;
import com.lms.management.repository.AttendanceRecordRepository;
import com.lms.management.repository.StudentBatchRepository;
import com.lms.management.service.AttendanceSummaryService;
import com.lms.management.service.EmailNotificationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttendanceSummaryServiceImpl
        implements AttendanceSummaryService {

    private final AttendanceRecordRepository attendanceRecordRepository;
    private final AttendanceConfigRepository attendanceConfigRepository;
    private final EmailNotificationService emailNotificationService;
    private final StudentBatchRepository studentBatchRepository;

    @Override
    public Map<String, Object> getStudentEligibilitySummary(
            Long studentId,
            Long courseId,
            Long batchId
    ) {

        // 1️⃣ Fetch attendance records (RAW DATA)
        List<AttendanceRecord> records =
                attendanceRecordRepository.findByStudentId(studentId);

        // 2️⃣ Fetch config (THRESHOLDS)
        AttendanceConfig config =
                attendanceConfigRepository
                        .findByCourseIdAndBatchId(courseId, batchId)
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Attendance config not found"));

        long totalSessions = 0;
        long presentCount = 0;
        long absentCount = 0;

        for (AttendanceRecord record : records) {

            String status = record.getStatus();

            // EXCUSED = ignored
            if ("EXCUSED".equalsIgnoreCase(status)) {
                continue;
            }

            totalSessions++;

            if ("PRESENT".equalsIgnoreCase(status)
                    || "LATE".equalsIgnoreCase(status)
                    || "PARTIAL".equalsIgnoreCase(status)) {
                presentCount++;
            } else if ("ABSENT".equalsIgnoreCase(status)) {
                absentCount++;
            }
        }

        int attendancePercentage =
                totalSessions == 0
                        ? 0
                        : (int) ((presentCount * 100) / totalSessions);

        boolean atRisk =
                attendancePercentage < config.getAtRiskPercent();

        boolean examEligible =
                attendancePercentage >=
                        config.getExamEligibilityPercent();

        // 3️⃣ Response
        Map<String, Object> response = new HashMap<>();
        response.put("totalSessions", totalSessions);
        response.put("presentCount", presentCount);
        response.put("absentCount", absentCount);
        response.put("attendancePercentage", attendancePercentage);
        response.put("atRisk", atRisk);
        response.put("examEligible", examEligible);
        
        if (atRisk) {
            emailNotificationService.sendAttendanceAlert(
                    studentId,
                    "AT_RISK",
                    attendancePercentage
            );
        }

        return response;
    }
    
    @Override
    public Map<String, Object> getBatchSummary(Long courseId, Long batchId) {

        List<Long> studentIds =
                studentBatchRepository.findStudentIdsByBatchId(batchId);

        int totalStudents = studentIds.size();
        int atRiskCount = 0;
        int eligibleCount = 0;
        int totalPercent = 0;

        for (Long studentId : studentIds) {
            Map<String, Object> summary =
                    getStudentEligibilitySummary(studentId, courseId, batchId);

            Object percentObj = summary.get("attendancePercent");
            int percent = percentObj == null ? 0 : ((Number) percentObj).intValue();
            
            totalPercent += percent;
        }

        for (Long studentId : studentIds) {

            Map<String, Object> summary =
                    getStudentEligibilitySummary(studentId, courseId, batchId);

            Integer percentObj = (Integer) summary.get("attendancePercent");
            Boolean atRiskObj = (Boolean) summary.get("atRisk");
            Boolean eligibleObj = (Boolean) summary.get("eligible");

            int percent = percentObj != null ? percentObj : 0;
            boolean atRisk = Boolean.TRUE.equals(atRiskObj);
            boolean eligible = Boolean.TRUE.equals(eligibleObj);

            totalPercent += percent;

            if (atRisk) atRiskCount++;
            if (eligible) eligibleCount++;
        }

        int avgPercent =
                totalStudents == 0 ? 0 : totalPercent / totalStudents;

        Map<String, Object> response = new HashMap<>();
        response.put("totalStudents", totalStudents);
        response.put("atRiskCount", atRiskCount);
        response.put("eligibleCount", eligibleCount);
        response.put("averageAttendancePercent", avgPercent);

        return response;
    }
}
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
import com.lms.management.service.AttendanceSummaryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttendanceSummaryServiceImpl implements AttendanceSummaryService {

    private final AttendanceRecordRepository attendanceRecordRepository;
    private final AttendanceConfigRepository attendanceConfigRepository;

    @Override
    public Map<String, Object> getStudentEligibilitySummary(
            Long studentId,
            Long courseId,
            Long batchId
    ) {

        List<AttendanceRecord> records =
                attendanceRecordRepository.findByStudentId(studentId);

        AttendanceConfig config =
                attendanceConfigRepository
                        .findByCourseIdAndBatchId(courseId, batchId)
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Attendance config not found"
                                ));

        long totalSessions = 0;
        long attendedSessions = 0;

        for (AttendanceRecord record : records) {

            String status = record.getStatus();

            // LEAVE / EXCUSED → neutral, ignore completely
            if ("EXCUSED".equalsIgnoreCase(status)) {
                continue;
            }

            totalSessions++;

            if ("PRESENT".equalsIgnoreCase(status)
                    || "LATE".equalsIgnoreCase(status)
                    || "PARTIAL".equalsIgnoreCase(status)) {
                attendedSessions++;
            }
        }

        int attendancePercent =
                totalSessions == 0
                        ? 0
                        : (int) ((attendedSessions * 100) / totalSessions);

        boolean eligible =
                attendancePercent >=
                        config.getExamEligibilityPercent();

        boolean atRisk =
                attendancePercent <
                        config.getAtRiskPercent();

        Map<String, Object> summary = new HashMap<>();
        summary.put("attendancePercent", attendancePercent);
        summary.put("eligible", eligible);
        summary.put("atRisk", atRisk);

        return summary;
    }
}

package com.lms.management.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.management.model.AttendanceConfig;
import com.lms.management.model.StudentAttendanceStatus;
import com.lms.management.repository.AttendanceConfigRepository;
import com.lms.management.repository.StudentBatchRepository;
import com.lms.management.service.AttendanceRecordService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttendanceDashboardServiceImpl {

    private final StudentBatchRepository studentBatchRepository;
    private final AttendanceRecordService attendanceRecordService;
    private final AttendanceConfigRepository attendanceConfigRepository;

    /**
     * DASHBOARD LOGIC
     * One student at a time, calculated on demand
     */
    public List<StudentAttendanceStatus> getDashboardStatus(
            Long courseId,
            Long batchId
    ) {

        AttendanceConfig config =
                attendanceConfigRepository
                        .findByCourseIdAndBatchId(courseId, batchId)
                        .orElseThrow(() ->
                                new IllegalStateException("Attendance config not found"));

        // 1. Get all students in batch
        List<Long> studentIds =
                studentBatchRepository.findStudentIdsByBatchId(batchId);

        List<StudentAttendanceStatus> result = new ArrayList<>();

        // 2. Loop each student (IMPORTANT LOOP)
        for (Long studentId : studentIds) {

            int attendancePercent =
                    attendanceRecordService.getAttendancePercentage(
                            studentId,
                            courseId,
                            batchId
                    );

            boolean eligible =
                    attendancePercent >= config.getExamEligibilityPercent();

            boolean atRiskByPercent =
                    attendancePercent < config.getAtRiskPercent();

            boolean atRiskByAbsence =
                    attendanceRecordService.isStudentAtRiskByAbsence(
                            studentId,
                            courseId,
                            batchId
                    );

            // 3. Build dashboard row
            StudentAttendanceStatus status = new StudentAttendanceStatus();
            status.setStudentId(studentId);
            status.setAttendancePercent(attendancePercent);
            status.setEligible(eligible);
            status.setAtRiskByPercent(atRiskByPercent);
            status.setAtRiskByAbsence(atRiskByAbsence);
            result.add(status);
        }

        return result;
    }
}

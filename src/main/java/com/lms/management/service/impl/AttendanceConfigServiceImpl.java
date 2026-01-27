package com.lms.management.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.management.exception.ResourceNotFoundException;
import com.lms.management.model.AttendanceConfig;
import com.lms.management.repository.AttendanceConfigRepository;
import com.lms.management.service.AttendanceConfigService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AttendanceConfigServiceImpl implements AttendanceConfigService {

    private final AttendanceConfigRepository attendanceConfigRepository;

    // ===============================
    // CREATE CONFIG (ONLY ONCE)
    // ===============================
    @Override
    public AttendanceConfig createConfig(AttendanceConfig config) {

        attendanceConfigRepository
                .findByCourseIdAndBatchId(
                        config.getCourseId(),
                        config.getBatchId()
                )
                .ifPresent(c -> {
                    throw new IllegalStateException(
                            "Attendance config already exists for this batch"
                    );
                });

        return attendanceConfigRepository.save(config);
    }

    // ===============================
    // GET CONFIG (FOR UI)
    // ===============================
    @Override
    @Transactional(readOnly = true)
    public AttendanceConfig getConfig(Long courseId, Long batchId) {

        return attendanceConfigRepository
                .findByCourseIdAndBatchId(courseId, batchId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Attendance config not found"
                        )
                );
    }

    // ===============================
    // UPDATE CONFIG
    // ===============================
    @Override
    public AttendanceConfig updateConfig(
            Long configId,
            AttendanceConfig updatedConfig
    ) {

        AttendanceConfig existing =
                attendanceConfigRepository.findById(configId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Attendance config not found"
                                )
                        );

        // ⬇️ Update all editable fields
        existing.setAllowLate(updatedConfig.getAllowLate());
        existing.setLateAfterMinutes(updatedConfig.getLateAfterMinutes());
        existing.setAllowGraceTime(updatedConfig.getAllowGraceTime());
        existing.setGraceTimeMinutes(updatedConfig.getGraceTimeMinutes());
        existing.setAllowManualOverride(updatedConfig.getAllowManualOverride());
        existing.setAllowPartialAttendance(updatedConfig.getAllowPartialAttendance());
        existing.setAllowExcused(updatedConfig.getAllowExcused());
        existing.setRequireRemarksForAbsent(
                updatedConfig.getRequireRemarksForAbsent()
        );

        return attendanceConfigRepository.save(existing);
    }
}

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
    // UPDATE CONFIG (DB-ALIGNED)
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

        // ===============================
        // ACADEMIC THRESHOLDS
        // ===============================
        existing.setExamEligibilityPercent(
                updatedConfig.getExamEligibilityPercent()
        );
        existing.setAtRiskPercent(
                updatedConfig.getAtRiskPercent()
        );

        // ===============================
        // ATTENDANCE TIMING RULES
        // ===============================
        existing.setLateGraceMinutes(
                updatedConfig.getLateGraceMinutes()
        );
        existing.setMinPresenceMinutes(
                updatedConfig.getMinPresenceMinutes()
        );
        existing.setAutoAbsentMinutes(
                updatedConfig.getAutoAbsentMinutes()
        );

        // ===============================
        // EARLY EXIT
        // ===============================
        existing.setEarlyExitAction(
                updatedConfig.getEarlyExitAction()
        );

        // ===============================
        // CONTROLS
        // ===============================
        existing.setAllowOffline(
                updatedConfig.getAllowOffline()
        );
        existing.setAllowManualOverride(
                updatedConfig.getAllowManualOverride()
        );
        existing.setOneDevicePerSession(
                updatedConfig.getOneDevicePerSession()
        );
        existing.setLogIpAddress(
                updatedConfig.getLogIpAddress()
        );
        existing.setStrictStart(
                updatedConfig.getStrictStart()
        );
        existing.setQrCodeMode(
                updatedConfig.getQrCodeMode()
        );

        // ===============================
        // GRACE & ALERTS
        // ===============================
        existing.setGracePeriodMinutes(
                updatedConfig.getGracePeriodMinutes()
        );
        existing.setConsecutiveAbsenceLimit(
                updatedConfig.getConsecutiveAbsenceLimit()
        );

        return attendanceConfigRepository.save(existing);
    }
}

package com.lms.management.service;

import com.lms.management.model.AttendanceConfig;

public interface AttendanceConfigService {

    // ===============================
    // CREATE (only once per course+batch)
    // ===============================
    AttendanceConfig createConfig(AttendanceConfig config);

    // ===============================
    // GET CONFIG (for UI load)
    // ===============================
    AttendanceConfig getConfig(
            Long courseId,
            Long batchId
    );

    // ===============================
    // UPDATE CONFIG (toggles, sliders)
    // ===============================
    AttendanceConfig updateConfig(
            Long configId,
            AttendanceConfig updatedConfig
    );
}

package com.lms.management.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.lms.management.model.AttendanceConfig;
import com.lms.management.service.AttendanceConfigService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/attendance/config")
@RequiredArgsConstructor
public class AttendanceConfigController {

    private final AttendanceConfigService attendanceConfigService;

    // ===============================
    // CREATE CONFIG (ADMIN ONLY)
    // ===============================
    @PostMapping
    @PreAuthorize("hasAuthority('ATTENDANCE_CONFIG_UPDATE')")
    public AttendanceConfig createConfig(
            @RequestBody AttendanceConfig config
    ) {
        return attendanceConfigService.createConfig(config);
    }

    // ===============================
    // GET CONFIG (UI LOAD)
    // ===============================
    @GetMapping
    @PreAuthorize("hasAuthority('ATTENDANCE_CONFIG_VIEW')")
    public AttendanceConfig getConfig(
            @RequestParam Long courseId,
            @RequestParam Long batchId
    ) {
        return attendanceConfigService.getConfig(courseId, batchId);
    }

    // ===============================
    // UPDATE CONFIG (ADMIN)
    // ===============================
    @PutMapping("/{configId}")
    @PreAuthorize("hasAuthority('ATTENDANCE_CONFIG_UPDATE')")
    public AttendanceConfig updateConfig(
            @PathVariable Long configId,
            @RequestBody AttendanceConfig config
    ) {
        return attendanceConfigService.updateConfig(configId, config);
    }
}

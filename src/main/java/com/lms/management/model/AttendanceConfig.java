package com.lms.management.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "attendance_config")
@Getter
@Setter
@NoArgsConstructor
public class AttendanceConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ===============================
    // CONTEXT
    // ===============================
    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "batch_id", nullable = false, unique = true)
    private Long batchId;

    // ===============================
    // ATTENDANCE RULES
    // ===============================
    private Boolean allowLate;
    private Integer lateAfterMinutes;

    private Boolean allowGraceTime;
    private Integer graceTimeMinutes;

    private Boolean allowPartialAttendance;

    private Boolean allowExcused;

    // ===============================
    // MANUAL CONTROL
    // ===============================
    private Boolean allowManualOverride;
    private Boolean requireRemarksForAbsent;

    // ===============================
    // SECURITY & DEVICE
    // ===============================
    private Boolean oneDevicePerSession;
    private Boolean logIpAddress;

    // ===============================
    // EXCEPTIONS
    // ===============================
    private Boolean allowCameraFailure;
    private Integer networkFailureGraceMinutes;
    private Boolean emergencyUnlock;

    // ===============================
    // ALERTS
    // ===============================
    private Boolean eligibilityRiskAlert;
    private Integer consecutiveAbsenceAlertLimit;

    // ===============================
    // ACADEMIC THRESHOLDS
    // ===============================
    private Integer examEligibilityThreshold;
    private Integer atRiskWarningThreshold;

    // ===============================
    // SESSION CONTROLS
    // ===============================
    private String qrCodeMode;
    private Boolean strictStartEnforcement;

    // ===============================
    // AUDIT
    // ===============================
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

package com.lms.management.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "attendance_config",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"course_id", "batch_id"})
    }
)
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

    @Column(name = "batch_id", nullable = false)
    private Long batchId;

    // ===============================
    // ACADEMIC THRESHOLDS
    // ===============================
    @Column(name = "exam_eligibility_percent", nullable = false)
    private Integer examEligibilityPercent;

    @Column(name = "at_risk_percent", nullable = false)
    private Integer atRiskPercent;

    // ===============================
    // ATTENDANCE TIMING RULES
    // ===============================
    @Column(name = "late_grace_minutes", nullable = false)
    private Integer lateGraceMinutes;

    @Column(name = "min_presence_minutes", nullable = false)
    private Integer minPresenceMinutes;

    @Column(name = "auto_absent_minutes", nullable = false)
    private Integer autoAbsentMinutes;

    // ===============================
    // EARLY EXIT
    // ===============================
    @Column(name = "early_exit_action", nullable = false)
    private String earlyExitAction; // MARK_PARTIAL / ABSENT

    // ===============================
    // CONTROLS
    // ===============================
    @Column(name = "allow_offline", nullable = false)
    private Boolean allowOffline;

    @Column(name = "allow_manual_override", nullable = false)
    private Boolean allowManualOverride;

    @Column(name = "require_override_reason", nullable = false)
    private Boolean requireOverrideReason;

    @Column(name = "notify_parents", nullable = false)
    private Boolean notifyParents;

    @Column(name = "one_device_per_session", nullable = false)
    private Boolean oneDevicePerSession;

    @Column(name = "log_ip_address", nullable = false)
    private Boolean logIpAddress;

    @Column(name = "strict_start", nullable = false)
    private Boolean strictStart;

    @Column(name = "qr_code_mode", nullable = false)
    private String qrCodeMode; // ALWAYS / TIME_BASED

    // ===============================
    // GRACE & ALERTS
    // ===============================
    @Column(name = "grace_period_minutes", nullable = false)
    private Integer gracePeriodMinutes;

    @Column(name = "consecutive_absence_limit", nullable = false)
    private Integer consecutiveAbsenceLimit;

    // ===============================
    // AUDIT
    // ===============================
    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "updated_by", nullable = false)
    private Long updatedBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
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

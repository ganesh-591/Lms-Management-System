package com.lms.management.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "attendance_alert_flag",
    uniqueConstraints = {
        @UniqueConstraint(
            columnNames = {
                "student_id",
                "course_id",
                "batch_id",
                "flag_type"
            }
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
public class AttendanceAlertFlag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ===============================
    // CONTEXT
    // ===============================
    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "batch_id", nullable = false)
    private Long batchId;

    // AT_RISK_PERCENT / CONSECUTIVE_ABSENCE / NOT_ELIGIBLE
    @Column(name = "flag_type", nullable = false, length = 50)
    private String flagType;

    // ACTIVE / RESOLVED
    @Column(nullable = false, length = 20)
    private String status;

    // Snapshot message (optional, for email / audit)
    @Column(length = 500)
    private String message;

    // ===============================
    // AUDIT
    // ===============================
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @PrePersist
    protected void onCreate() {
        this.status = "ACTIVE";
        this.createdAt = LocalDateTime.now();
    }
}

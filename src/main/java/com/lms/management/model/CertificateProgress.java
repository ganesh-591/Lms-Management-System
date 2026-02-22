package com.lms.management.model;

import java.time.LocalDateTime;

import com.lms.management.enums.CertificateEligibilityStatus;
import com.lms.management.enums.TargetType;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "certificate_progress",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_user_target_progress",
                        columnNames = {"user_id", "target_type", "target_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CertificateProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    // Same enum style as Certificate (ORDINAL by default)
    @Column(name = "target_type", nullable = false)
    private TargetType targetType;

    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @Column(name = "completion_percent")
    private Double completionPercent;

    private Double score;

    @Column(name = "attendance_percent")
    private Double attendancePercent;

    @Column(name = "submission_completed")
    private Boolean submissionCompleted;

    @Enumerated(EnumType.STRING)
    @Column(name = "eligibility_status", nullable = false)
    private CertificateEligibilityStatus eligibilityStatus;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
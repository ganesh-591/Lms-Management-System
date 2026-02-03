package com.lms.management.model;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "exam_proctoring",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "exam_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
public class ExamProctoring {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "proctoring_id")
    private Long proctoringId;

    @Column(name = "exam_id", nullable = false)
    private Long examId;

    // Enable / disable proctoring
    @Column(name = "enabled", nullable = false)
    private Boolean enabled;

    // Camera mandatory or not
    @Column(name = "camera_required", nullable = false)
    private Boolean cameraRequired;

    // System checks (device / browser validation)
    @Column(name = "system_check_required", nullable = false)
    private Boolean systemCheckRequired;

    // Max violations before auto-submit / terminate
    @Column(name = "violation_limit", nullable = false)
    private Integer violationLimit;
}

package com.lms.management.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "exam_violation")
@Getter
@Setter
@NoArgsConstructor
public class ExamViolation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "violation_id")
    private Long violationId;

    @Column(name = "attempt_id", nullable = false)
    private Long attemptId;

    @Column(name = "violation_type", nullable = false, length = 50)
    private String violationType;
    // TAB_SWITCH / FULL_SCREEN_EXIT / DEVICE_CHANGE / IP_CHANGE

    @Column(name = "violation_time", nullable = false)
    private LocalDateTime violationTime;
}

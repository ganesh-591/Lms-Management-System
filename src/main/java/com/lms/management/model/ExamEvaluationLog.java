package com.lms.management.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "exam_evaluation_log")
@Getter
@Setter
@NoArgsConstructor
public class ExamEvaluationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long logId;

    @Column(name = "attempt_id", nullable = false)
    private Long attemptId;

    @Column(name = "evaluator_id", nullable = false)
    private Long evaluatorId; // admin / instructor

    @Column(name = "old_score", nullable = false)
    private Double oldScore;

    @Column(name = "new_score", nullable = false)
    private Double newScore;

    @Column(name = "reason", nullable = false, length = 255)
    private String reason;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}

package com.lms.management.model;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "exam_grading",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "exam_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
public class ExamGrading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "grading_id")
    private Long gradingId;

    @Column(name = "exam_id", nullable = false)
    private Long examId;

    // Auto evaluate MCQ questions
    @Column(name = "auto_evaluation", nullable = false)
    private Boolean autoEvaluation;

    // Allow partial marking (mainly descriptive)
    @Column(name = "partial_marking", nullable = false)
    private Boolean partialMarking;

    // Show result to student
    @Column(name = "show_result", nullable = false)
    private Boolean showResult;

    // Display rank in result
    @Column(name = "show_rank", nullable = false)
    private Boolean showRank;

    // Display percentile in result
    @Column(name = "show_percentile", nullable = false)
    private Boolean showPercentile;
}

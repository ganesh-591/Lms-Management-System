package com.lms.management.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "exam_response",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"attempt_id", "exam_question_id"})
    }
)
@Getter
@Setter
@NoArgsConstructor
public class ExamResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "response_id")
    private Long responseId;

    @Column(name = "attempt_id", nullable = false)
    private Long attemptId;

    @Column(name = "exam_question_id", nullable = false)
    private Long examQuestionId;

    // ================= MCQ =================
    @Column(name = "selected_option_id")
    private Long selectedOptionId;

    // ================= DESCRIPTIVE =================
    @Column(name = "descriptive_answer", columnDefinition = "TEXT")
    private String descriptiveAnswer;

    // ================= CODING (ENTERPRISE READY) =================
    @Column(name = "coding_submission_code", columnDefinition = "LONGTEXT")
    private String codingSubmissionCode;

    // ================= EVALUATION =================
    @Column(name = "marks_awarded")
    private Double marksAwarded;

    @Column(name = "evaluation_type")
    private String evaluationType;
    // AUTO / MANUAL
}

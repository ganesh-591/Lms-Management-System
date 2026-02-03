package com.lms.management.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "exam_question",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"exam_id", "question_id"})
    }
)
@Getter
@Setter
@NoArgsConstructor
public class ExamQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exam_question_id")
    private Long examQuestionId;

    @Column(name = "exam_id", nullable = false)
    private Long examId;

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @Column(name = "marks", nullable = false)
    private Double marks;

    @Column(name = "question_order", nullable = false)
    private Integer questionOrder;
}

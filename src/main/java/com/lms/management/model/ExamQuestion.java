package com.lms.management.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "exam_question",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"exam_section_id", "question_id"})
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

    // 🔥 Now linked to ExamSection (not directly to Exam)
    @Column(name = "exam_section_id", nullable = false)
    private Long examSectionId;

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "question_id",
        referencedColumnName = "question_id",
        insertable = false,
        updatable = false
    )
    private Question question;

    @Column(name = "marks", nullable = false)
    private Double marks;

    @Column(name = "question_order", nullable = false)
    private Integer questionOrder;
}
package com.lms.management.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "exam_section")
@Getter
@Setter
@NoArgsConstructor
public class ExamSection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exam_section_id")
    private Long examSectionId;

    @Column(name = "exam_id", nullable = false)
    private Long examId;

    @Column(name = "section_id", nullable = false)
    private Long sectionId;

    // Order of section inside exam
    @Column(name = "section_order")
    private Integer sectionOrder;

    // Optional override
    @Column(name = "shuffle_questions")
    private Boolean shuffleQuestions;
}
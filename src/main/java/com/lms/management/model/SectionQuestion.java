package com.lms.management.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "section_question")
@Getter
@Setter
@NoArgsConstructor
public class SectionQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "section_question_id")
    private Long sectionQuestionId;

    @Column(name = "section_id", nullable = false)
    private Long sectionId;

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @Column(name = "display_order")
    private Integer displayOrder;
}
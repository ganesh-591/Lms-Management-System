package com.lms.management.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "question")
@Getter
@Setter
@NoArgsConstructor
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long questionId;

    @Column(name = "question_text", nullable = false, length = 1000)
    private String questionText;

    @Column(name = "question_type", nullable = false)
    private String questionType;
    // MCQ / DESCRIPTIVE / CODING

    // ✅ Only used when questionType = CODING
    @Enumerated(EnumType.STRING)
    @Column(name = "programming_language")
    private ProgrammingLanguage programmingLanguage;
}

package com.lms.management.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "question_descriptive_answer",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"question_id"})
    }
)
@Getter
@Setter
@NoArgsConstructor
public class QuestionDescriptiveAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "descriptive_answer_id")
    private Long descriptiveAnswerId;

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    // ✅ Admin model answer
    @Column(name = "answer_text", nullable = false, columnDefinition = "TEXT")
    private String answerText;

    // ✅ Comma separated keywords
    // Example: "inheritance, polymorphism, encapsulation"
    @Column(name = "keywords", columnDefinition = "TEXT")
    private String keywords;

    // ✅ Marks awarded per keyword match
    @Column(name = "keyword_weight")
    private Double keywordWeight;

    // Optional manual evaluation notes
    @Column(name = "guidelines", columnDefinition = "TEXT")
    private String guidelines;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
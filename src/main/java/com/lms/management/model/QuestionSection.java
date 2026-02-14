package com.lms.management.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "question_section")
@Getter
@Setter
@NoArgsConstructor
public class QuestionSection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "section_id")
    private Long sectionId;

    @Column(name = "section_name", nullable = false)
    private String sectionName;

    @Column(name = "section_description", columnDefinition = "TEXT")
    private String sectionDescription;

    // ✅ NEW — shuffle questions inside this section
    @Column(name = "shuffle_questions", nullable = false)
    private Boolean shuffleQuestions = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
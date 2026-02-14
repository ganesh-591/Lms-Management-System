package com.lms.management.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "question_option")
@Getter
@Setter
@NoArgsConstructor
public class QuestionOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_id")
    private Long optionId;

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    // 🔥 Now optional
    @Column(name = "option_text", columnDefinition = "TEXT")
    private String optionText;

    // 🔥 NEW — optional image
    @Column(name = "option_image_url")
    private String optionImageUrl;

    @Column(name = "is_correct", nullable = false)
    private Boolean isCorrect;
}
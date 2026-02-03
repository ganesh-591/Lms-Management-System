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
@Table(name = "exam_settings")
@Getter
@Setter
@NoArgsConstructor
public class ExamSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "settings_id")
    private Long settingsId;

    @Column(name = "exam_id", nullable = false)
    private Long examId;

    // Max attempts per student
    @Column(name = "attempts_allowed", nullable = false)
    private Integer attemptsAllowed;

    // Negative marking enabled / disabled
    @Column(name = "negative_marking", nullable = false)
    private Boolean negativeMarking;

    // Deduction value for wrong answers
    @Column(name = "negative_mark_value")
    private Double negativeMarkValue;

    // Shuffle questions
    @Column(name = "shuffle_questions", nullable = false)
    private Boolean shuffleQuestions;

    // Shuffle options
    @Column(name = "shuffle_options", nullable = false)
    private Boolean shuffleOptions;

    // Allow late entry into exam
    @Column(name = "allow_late_entry", nullable = false)
    private Boolean allowLateEntry;

    // LENIENT / STRICT
    @Column(name = "network_mode", nullable = false)
    private String networkMode;
}

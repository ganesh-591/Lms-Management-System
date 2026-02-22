package com.lms.management.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

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
@Table(name = "exam")
@Getter
@Setter
@NoArgsConstructor
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exam_id")
    private Long examId;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "batch_id")
    private Long batchId;

    @JsonProperty("examType") // ✅ EXPLICIT MAPPING
    @Column(name = "exam_type", nullable = false)
    private String examType;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "total_marks", nullable = false)
    private Integer totalMarks;

    @Column(name = "pass_percentage", nullable = false)
    private Double passPercentage;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;
    
    @Column(name = "certificate_enabled", nullable = false)
    private Boolean certificateEnabled = false;

    @PrePersist
    protected void onCreate() {
        this.status = "DRAFT";
        this.createdAt = LocalDateTime.now();
        
        
    }
}

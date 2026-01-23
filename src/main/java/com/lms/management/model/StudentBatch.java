package com.lms.management.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "student_batch")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "student_batch_id")
    private Long studentBatchId;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    // SNAPSHOT FIELD (display only)
    @Column(name = "student_name", nullable = false)
    private String studentName;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "batch_id", nullable = false)
    private Long batchId;

    @Column(name = "status")
    private String status; // ACTIVE / TRANSFERRED / COMPLETED

    @Column(name = "joined_at", updatable = false)
    private LocalDateTime joinedAt;

    @PrePersist
    protected void onCreate() {
        this.joinedAt = LocalDateTime.now();
        this.status = "ACTIVE";
    }
}

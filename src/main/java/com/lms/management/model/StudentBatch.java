package com.lms.management.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "student_batch")
@Getter
@Setter
public class StudentBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long studentBatchId;

    @Column(nullable = false)
    private Long studentId;

    @Column(nullable = false)
    private String studentName;

    @Column(nullable = false)
    private String studentEmail;

    @Column(nullable = false)
    private Long courseId;

    @Column(nullable = false)
    private Long batchId;

    @Column(nullable = false)
    private String status; // ACTIVE / TRANSFERRED / COMPLETED

    @Column(nullable = false)
    private LocalDateTime joinedAt;
}

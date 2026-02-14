package com.lms.management.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "batch")
@Getter
@Setter
@NoArgsConstructor
public class Batch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "batch_id")
    private Long batchId;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "batch_name", nullable = false)
    private String batchName;

    @Column(name = "trainer_id", nullable = false)
    private Long trainerId;

    @Column(name = "trainer_name", nullable = false)
    private String trainerName;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "max_students")
    private Integer maxStudents;

    // ===============================
    // 💰 FREE OR PAID CONTROL
    // ===============================
    @Column(name = "free_batch", nullable = false)
    private Boolean freeBatch;

    @Column(name = "fee")
    private Double fee;

    // ===============================
    // 🔒 CONTENT ACCESS CONTROL
    // ===============================
    @Column(name = "content_access", nullable = false)
    private Boolean contentAccess;

    @Column(name = "status")
    private String status; // Upcoming / Running / Completed

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.status = "Upcoming";

        // Default values
        if (this.freeBatch == null) {
            this.freeBatch = true; // default FREE
        }

        if (this.contentAccess == null) {
            this.contentAccess = false; // default blocked
        }

        // If FREE → remove fee
        if (Boolean.TRUE.equals(this.freeBatch)) {
            this.fee = null;
        }

        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {

        // If FREE → always nullify fee
        if (Boolean.TRUE.equals(this.freeBatch)) {
            this.fee = null;
        }

        this.updatedAt = LocalDateTime.now();
    }
}
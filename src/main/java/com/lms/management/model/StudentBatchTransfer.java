package com.lms.management.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "student_batch_transfer")
@Getter
@Setter
@NoArgsConstructor
public class StudentBatchTransfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transfer_id")
    private Long transferId;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "from_batch_id", nullable = false)
    private Long fromBatchId;

    @Column(name = "to_batch_id", nullable = false)
    private Long toBatchId;

    @Column(name = "reason", length = 500)
    private String reason;

    @Column(name = "transferred_by", nullable = false)
    private String transferredBy; // admin username or id

    @Column(name = "transferred_at", updatable = false)
    private LocalDateTime transferredAt;

    @PrePersist
    protected void onCreate() {
        this.transferredAt = LocalDateTime.now();
    }
}

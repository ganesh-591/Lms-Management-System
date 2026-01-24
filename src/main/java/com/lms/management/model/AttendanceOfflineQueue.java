package com.lms.management.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "attendance_offline_queue")
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class AttendanceOfflineQueue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // LMS session
    @Column(name = "session_id", nullable = false)
    private Long sessionId;

    // Batch context
    @Column(name = "batch_id", nullable = false)
    private Long batchId;

    // Student (from User module)
    @Column(name = "student_id", nullable = false)
    private Long studentId;

    // PRESENT / ABSENT / LATE / EXCUSED / PARTIAL
    @Column(nullable = false)
    private String status;

    @Column
    private String remarks;

    // When attendance was marked offline
    @Column(name = "queued_at", nullable = false)
    private LocalDateTime queuedAt;

    // Whether synced to attendance_record
    @Column(nullable = false)
    private Boolean synced;

    @PrePersist
    protected void onCreate() {
        this.queuedAt = LocalDateTime.now();
        this.synced = false;
    }
}

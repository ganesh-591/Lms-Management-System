package com.lms.management.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
	    name = "attendance_session",
	    uniqueConstraints = {
	        @UniqueConstraint(
	            columnNames = {"session_id", "batch_id"}
	        )
	    }
	)
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class AttendanceSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔗 Link to existing Session table
    @Column(name = "session_id", nullable = false)
    private Long sessionId;

    // 🔗 Batch (copied from session for fast access)
    @Column(name = "batch_id", nullable = false)
    private Long batchId;

    // 🔗 Course (useful for reports & filtering)
    @Column(name = "course_id", nullable = false)
    private Long courseId;

    // ACTIVE / ENDED
    @Column(nullable = false)
    private String status;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    // Trainer / Admin who started attendance
    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.status = "ACTIVE";
        this.startedAt = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
    }
}

package com.lms.management.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "attendance_upload_job")
@Getter
@Setter
@NoArgsConstructor
public class AttendanceUploadJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Context
    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Column(name = "batch_id", nullable = false)
    private Long batchId;

    // Optional mapping
    @Column(name = "session_id")
    private Long sessionId;

    // CSV date
    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;

    // Stored file path
    @Column(name = "file_path", nullable = false)
    private String filePath;

    // PENDING / PROCESSED / FAILED
    @Column(nullable = false)
    private String status;

    // Admin who uploaded
    @Column(name = "uploaded_by", nullable = false)
    private Long uploadedBy;

    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt;

    @PrePersist
    protected void onCreate() {
        this.uploadedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = "PENDING";
        }
    }
}

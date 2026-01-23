package com.lms.management.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "attendance_record")
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class AttendanceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔗 Link to attendance_session
    @Column(name = "attendance_session_id", nullable = false)
    private Long attendanceSessionId;

    // 🔗 Student info (from User module)
    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Column(name = "student_name", nullable = false)
    private String studentName;

    // PRESENT / ABSENT / LATE / EXCUSED
    @Column(nullable = false)
    private String status;

    @Column
    private String remarks;

    @Column(name = "marked_by", nullable = false)
    private Long markedBy;

    @Column(name = "marked_at", nullable = false)
    private LocalDateTime markedAt;

    // MANUAL / CSV / OFFLINE
    @Column(nullable = false)
    private String source;

    @PrePersist
    protected void onCreate() {
        this.markedAt = LocalDateTime.now();
    }
}

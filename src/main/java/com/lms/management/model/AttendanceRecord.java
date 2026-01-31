package com.lms.management.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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

    // PRESENT / ABSENT / LATE / EXCUSED / PARTIAL
    @Column(nullable = false)
    private String status;

    @Column
    private String remarks;
    
    @Column(name = "left_at")
    private LocalDateTime leftAt;

    @Column(name = "marked_by", nullable = false)
    private Long markedBy;

    @Column(name = "marked_at", nullable = false)
    private LocalDateTime markedAt;

    // ✅ REQUIRED FOR REPORTS & UI DATE FILTER
    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;

    // MANUAL / CSV / OFFLINE
    @Column(nullable = false)
    private String source;
    
    @Column(name = "student_id")
    private Long studentId;

    @PrePersist
    protected void onCreate() {
        this.markedAt = LocalDateTime.now();

        // Auto-set attendance date if not provided
        if (this.attendanceDate == null) {
            this.attendanceDate = LocalDate.now();
        }
    }
}

package com.lms.management.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "course_batch_stats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CourseBatchStats {

    @Id
    @Column(name = "course_id")
    private Long courseId;

    @Column(name = "total_batches")
    private int totalBatches;

    @Column(name = "running_batches")
    private int runningBatches;

    @Column(name = "upcoming_batches")
    private int upcomingBatches;

    @Column(name = "completed_batches")
    private int completedBatches;

    @Column(name = "parallel_batches")
    private int parallelBatches;

    @Column(name = "required_trainers")
    private int requiredTrainers;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}

package com.lms.management.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lms.management.model.CourseBatchStats;

public interface CourseBatchStatsRepository
        extends JpaRepository<CourseBatchStats, Long> {

    // courseId is the PRIMARY KEY
    // Default JpaRepository methods are enough for now
}

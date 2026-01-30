package com.lms.management.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lms.management.model.AttendanceAlertFlag;

public interface AttendanceAlertFlagRepository
        extends JpaRepository<AttendanceAlertFlag, Long> {

    Optional<AttendanceAlertFlag>
    findByStudentIdAndCourseIdAndBatchIdAndFlagType(
            Long studentId,
            Long courseId,
            Long batchId,
            String flagType
    );
}

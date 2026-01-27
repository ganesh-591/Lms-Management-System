package com.lms.management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.management.model.AttendanceUploadJob;

@Repository
public interface AttendanceUploadJobRepository
        extends JpaRepository<AttendanceUploadJob, Long> {

    // View all jobs of a batch
    List<AttendanceUploadJob> findByBatchId(Long batchId);

    // View jobs by status (PENDING / PROCESSED / FAILED)
    List<AttendanceUploadJob> findByStatus(String status);

    // Admin upload history
    List<AttendanceUploadJob> findByUploadedBy(Long uploadedBy);
}

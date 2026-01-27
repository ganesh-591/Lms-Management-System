package com.lms.management.service;

import java.util.List;

import com.lms.management.model.AttendanceUploadJob;

public interface AttendanceUploadJobService {

    // ===============================
    // CREATE UPLOAD JOB (CSV upload)
    // ===============================
    AttendanceUploadJob createJob(AttendanceUploadJob job);

    // ===============================
    // UPDATE JOB STATUS
    // ===============================
    AttendanceUploadJob updateStatus(
            Long jobId,
            String status
    );

    // ===============================
    // VIEW JOBS
    // ===============================
    List<AttendanceUploadJob> getByBatch(Long batchId);

    List<AttendanceUploadJob> getByStatus(String status);
    
    

    List<AttendanceUploadJob> getByUploader(Long uploadedBy);
}

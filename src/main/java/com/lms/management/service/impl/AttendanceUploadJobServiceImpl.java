package com.lms.management.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.management.exception.ResourceNotFoundException;
import com.lms.management.model.AttendanceUploadJob;
import com.lms.management.repository.AttendanceUploadJobRepository;
import com.lms.management.service.AttendanceUploadJobService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AttendanceUploadJobServiceImpl implements AttendanceUploadJobService {

    private final AttendanceUploadJobRepository attendanceUploadJobRepository;

    // ===============================
    // CREATE UPLOAD JOB
    // ===============================
    @Override
    public AttendanceUploadJob createJob(AttendanceUploadJob job) {
        job.setStatus("PENDING");
        job.setUploadedAt(LocalDateTime.now());
        return attendanceUploadJobRepository.save(job);
    }

    // ===============================
    // UPDATE JOB STATUS
    // ===============================
    @Override
    public AttendanceUploadJob updateStatus(Long jobId, String status) {

        AttendanceUploadJob job =
                attendanceUploadJobRepository.findById(jobId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Upload job not found")
                        );

        job.setStatus(status);
        return attendanceUploadJobRepository.save(job);
    }

    // ===============================
    // VIEW JOBS
    // ===============================
    @Override
    @Transactional(readOnly = true)
    public List<AttendanceUploadJob> getByBatch(Long batchId) {
        return attendanceUploadJobRepository.findByBatchId(batchId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceUploadJob> getByStatus(String status) {
        return attendanceUploadJobRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceUploadJob> getByUploader(Long uploadedBy) {
        return attendanceUploadJobRepository.findByUploadedBy(uploadedBy);
    }
}

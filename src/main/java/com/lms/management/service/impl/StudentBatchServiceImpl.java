package com.lms.management.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.management.exception.ResourceNotFoundException;
import com.lms.management.model.Batch;
import com.lms.management.model.StudentBatch;
import com.lms.management.repository.BatchRepository;
import com.lms.management.repository.StudentBatchRepository;
import com.lms.management.service.StudentBatchService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentBatchServiceImpl implements StudentBatchService {

    private final StudentBatchRepository studentBatchRepository;
    private final BatchRepository batchRepository;

    // ================= ENROLL =================
    @Override
    public StudentBatch enrollStudent(StudentBatch studentBatch) {

        Long studentId = studentBatch.getStudentId();
        Long courseId  = studentBatch.getCourseId();
        Long batchId   = studentBatch.getBatchId();

        // ❌ Same batch duplicate
        if (studentBatchRepository
                .existsByStudentIdAndBatchIdAndStatus(
                        studentId, batchId, "ACTIVE")) {
            throw new IllegalStateException(
                    "Student already active in this batch");
        }

        // ❌ Same course multiple active batches
        if (studentBatchRepository
                .existsByStudentIdAndCourseIdAndStatus(
                        studentId, courseId, "ACTIVE")) {
            throw new IllegalStateException(
                    "Student already has an active batch for this course");
        }

        // 🔹 Load batch
        Batch batch = batchRepository.findById(batchId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Batch not found"));

        // 🔹 Count active students
        long activeCount =
                studentBatchRepository
                        .countByBatchIdAndStatus(batchId, "ACTIVE");

        // 🔹 If batch is full → auto-create new batch
        if (batch.getMaxStudents() != null
                && activeCount >= batch.getMaxStudents()) {

            throw new IllegalStateException(
                "Batch is full. Please create a new batch."
            );
        }

        // 🔹 Enroll student into final batch
        studentBatch.setBatchId(batch.getBatchId());
        studentBatch.setStatus("ACTIVE");
        studentBatch.setJoinedAt(LocalDateTime.now());

        return studentBatchRepository.save(studentBatch);
    }

    // ================= UPDATE =================
    @Override
    public StudentBatch updateEnrollment(Long studentBatchId, StudentBatch updated) {

        StudentBatch existing = studentBatchRepository.findById(studentBatchId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Enrollment not found"));

        if (updated.getStatus() != null) {
            existing.setStatus(updated.getStatus());
        }

        return studentBatchRepository.save(existing);
    }

    // ================= VIEW BY BATCH =================
    @Override
    public List<StudentBatch> getStudentsByBatch(Long batchId) {
        return studentBatchRepository.findByBatchId(batchId);
    }

    // ================= VIEW OWN =================
    @Override
    public StudentBatch getStudentCurrentBatch(Long studentId) {
        return studentBatchRepository
                .findFirstByStudentIdAndStatus(studentId, "ACTIVE")
                .orElseThrow(() ->
                        new ResourceNotFoundException("Active batch not found"));
    }

    // ================= REMOVE =================
    @Override
    public void removeStudent(Long studentBatchId) {
        studentBatchRepository.deleteById(studentBatchId);
    }
}

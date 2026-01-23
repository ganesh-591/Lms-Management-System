package com.lms.management.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.management.exception.ResourceNotFoundException;
import com.lms.management.model.StudentBatch;
import com.lms.management.repository.StudentBatchRepository;
import com.lms.management.service.StudentBatchService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentBatchServiceImpl implements StudentBatchService {

    private final StudentBatchRepository studentBatchRepository;

    // ================= ENROLL =================
    @Override
    public StudentBatch enrollStudent(
            Long studentId,
            String studentName,
            Long courseId,
            Long batchId) {

        StudentBatch studentBatch = new StudentBatch();
        studentBatch.setStudentId(studentId);
        studentBatch.setStudentName(studentName);
        studentBatch.setCourseId(courseId);
        studentBatch.setBatchId(batchId);
        studentBatch.setStatus("ACTIVE");
        studentBatch.setJoinedAt(LocalDateTime.now());

        return studentBatchRepository.save(studentBatch);
    }

    // ================= UPDATE (PUT = PATCH) =================
    @Override
    public StudentBatch updateEnrollment(
            Long studentBatchId,
            StudentBatch updated) {

        StudentBatch existing = studentBatchRepository.findById(studentBatchId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Student enrollment not found"));

        if (updated.getStatus() != null) {
            existing.setStatus(updated.getStatus());
        }

        if (updated.getBatchId() != null) {
            existing.setBatchId(updated.getBatchId());
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
                        new ResourceNotFoundException("Active batch not found for student"));
    }

    // ================= REMOVE =================
    @Override
    public void removeStudent(Long studentBatchId) {

        StudentBatch existing = studentBatchRepository.findById(studentBatchId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Student enrollment not found"));

        studentBatchRepository.delete(existing);
    }
}

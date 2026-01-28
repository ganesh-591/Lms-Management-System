package com.lms.management.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.management.exception.ResourceNotFoundException;
import com.lms.management.model.StudentBatch;
import com.lms.management.model.StudentBatchTransfer;
import com.lms.management.repository.StudentBatchRepository;
import com.lms.management.repository.StudentBatchTransferRepository;
import com.lms.management.service.StudentBatchTransferService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentBatchTransferServiceImpl
        implements StudentBatchTransferService {

    private final StudentBatchRepository studentBatchRepository;
    private final StudentBatchTransferRepository transferRepository;

    // ================= TRANSFER STUDENT =================
    @Override
    public StudentBatchTransfer transferStudent(
            Long studentId,
            Long courseId,
            Long toBatchId,
            String reason,
            String transferredBy) {

        StudentBatch activeBatch =
                studentBatchRepository
                        .findFirstByStudentIdAndStatus(studentId, "ACTIVE")
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Active batch not found for student"));

        Long fromBatchId = activeBatch.getBatchId();

        // ❗ Block same batch transfer
        if (fromBatchId.equals(toBatchId)) {
            throw new IllegalStateException(
                    "Source batch and target batch cannot be the same");
        }

        // ❗ Block duplicate ACTIVE in target batch
        if (studentBatchRepository
                .existsByStudentIdAndBatchIdAndStatus(
                        studentId, toBatchId, "ACTIVE")) {

            throw new IllegalStateException(
                    "Student is already active in target batch");
        }

        // Close old enrollment
        activeBatch.setStatus("TRANSFERRED");
        studentBatchRepository.save(activeBatch);

        // Create new ACTIVE enrollment
        StudentBatch newBatch = new StudentBatch();
        newBatch.setStudentId(studentId);
        newBatch.setCourseId(courseId);
        newBatch.setBatchId(toBatchId);
        newBatch.setStatus("ACTIVE");
        newBatch.setJoinedAt(LocalDateTime.now());

        studentBatchRepository.save(newBatch);

        // Save transfer history
        StudentBatchTransfer transfer = new StudentBatchTransfer();
        transfer.setStudentId(studentId);
        transfer.setCourseId(courseId);
        transfer.setFromBatchId(fromBatchId);
        transfer.setToBatchId(toBatchId);
        transfer.setReason(reason);
        transfer.setTransferredBy(transferredBy);
        transfer.setTransferredAt(LocalDateTime.now());

        return transferRepository.save(transfer);
    }

    // ================= VIEW BY STUDENT =================
    @Override
    public List<StudentBatchTransfer> getTransfersByStudent(Long studentId) {
        return transferRepository.findByStudentId(studentId);
    }

    // ================= VIEW BY COURSE =================
    @Override
    public List<StudentBatchTransfer> getTransfersByCourse(Long courseId) {
        return transferRepository.findByCourseId(courseId);
    }
}

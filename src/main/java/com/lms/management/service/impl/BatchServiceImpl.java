package com.lms.management.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.management.exception.ResourceNotFoundException;
import com.lms.management.model.Batch;
import com.lms.management.repository.BatchRepository;
import com.lms.management.service.BatchService;
import com.lms.management.service.CourseBatchStatsService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class BatchServiceImpl implements BatchService {

    private final BatchRepository batchRepository;
    private final CourseBatchStatsService courseBatchStatsService;

    // ================= CREATE =================
    @Override
    public Batch createBatch(Long courseId, Batch batch) {

        batch.setCourseId(courseId);

        if (batchRepository.existsByCourseIdAndBatchName(
                courseId, batch.getBatchName())) {

            throw new IllegalStateException(
                    "Batch already exists for this course"
            );
        }

        // 🔒 Business Rule
        if (Boolean.TRUE.equals(batch.getFreeBatch())) {
            batch.setFee(null);   // free → no fee
        } else {
            if (batch.getFee() == null || batch.getFee() <= 0) {
                throw new IllegalStateException("Paid batch must have valid fee");
            }
        }

        Batch savedBatch = batchRepository.save(batch);

        courseBatchStatsService.recalculateStats(courseId);

        return savedBatch;
    }

    // ================= GET BY ID =================
    @Override
    public Batch getBatchById(Long batchId) {

        return batchRepository.findById(batchId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Batch not found"));
    }

    // ================= GET BY COURSE =================
    @Override
    public List<Batch> getBatchesByCourseId(Long courseId) {

        return batchRepository.findByCourseId(courseId);
    }

    // ================= UPDATE =================
    @Override
    public Batch updateBatch(Long batchId, Batch updatedBatch) {

        Batch existingBatch = getBatchById(batchId);

        if (updatedBatch.getBatchName() != null) {
            existingBatch.setBatchName(updatedBatch.getBatchName());
        }

        if (updatedBatch.getTrainerName() != null) {
            existingBatch.setTrainerName(updatedBatch.getTrainerName());
        }

        if (updatedBatch.getTrainerId() != null) {
            existingBatch.setTrainerId(updatedBatch.getTrainerId());
        }

        if (updatedBatch.getStartDate() != null) {
            existingBatch.setStartDate(updatedBatch.getStartDate());
        }

        if (updatedBatch.getEndDate() != null) {
            existingBatch.setEndDate(updatedBatch.getEndDate());
        }

        if (updatedBatch.getMaxStudents() != null) {
            existingBatch.setMaxStudents(updatedBatch.getMaxStudents());
        }

        if (updatedBatch.getFreeBatch() != null) {
            existingBatch.setFreeBatch(updatedBatch.getFreeBatch());
        }

        if (updatedBatch.getFee() != null) {
            existingBatch.setFee(updatedBatch.getFee());
        }

        if (updatedBatch.getContentAccess() != null) {
            existingBatch.setContentAccess(updatedBatch.getContentAccess());
        }

        if (updatedBatch.getStatus() != null) {
            existingBatch.setStatus(updatedBatch.getStatus());
        }

        // 🔒 Business Rule (same as create)
        if (Boolean.TRUE.equals(existingBatch.getFreeBatch())) {
            existingBatch.setFee(null);
        } else {
            if (existingBatch.getFee() == null || existingBatch.getFee() <= 0) {
                throw new IllegalStateException("Paid batch must have valid fee");
            }
        }

        Batch savedBatch = batchRepository.save(existingBatch);

        courseBatchStatsService.recalculateStats(existingBatch.getCourseId());

        return savedBatch;
    }

    // ================= DELETE =================
    @Override
    public void deleteBatch(Long batchId) {

        Batch batch = getBatchById(batchId);
        Long courseId = batch.getCourseId();

        batchRepository.delete(batch);

        courseBatchStatsService.recalculateStats(courseId);
    }
}
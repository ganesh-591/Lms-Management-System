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

        // Force courseId from URL
        batch.setCourseId(courseId);

        // Prevent duplicate batch name under same course
        if (batchRepository.existsByCourseIdAndBatchName(
                courseId, batch.getBatchName())) {

            throw new IllegalStateException(
                    "Batch already exists for this course"
            );
        }

        Batch savedBatch = batchRepository.save(batch);

        // 🔹 Recalculate course batch stats
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

    // ================= UPDATE (PUT = PATCH) =================
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

        if (updatedBatch.getStatus() != null) {
            existingBatch.setStatus(updatedBatch.getStatus());
        }

        Batch savedBatch = batchRepository.save(existingBatch);

        // 🔹 Recalculate stats after update (status/date may change)
        courseBatchStatsService.recalculateStats(existingBatch.getCourseId());

        return savedBatch;
    }

    // ================= DELETE =================
    @Override
    public void deleteBatch(Long batchId) {

        Batch batch = getBatchById(batchId);
        Long courseId = batch.getCourseId();

        batchRepository.delete(batch);

        // 🔹 Recalculate stats after deletion
        courseBatchStatsService.recalculateStats(courseId);
    }
}

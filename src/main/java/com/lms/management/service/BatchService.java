package com.lms.management.service;

import java.util.List;

import com.lms.management.model.Batch;

public interface BatchService {

    // Create batch under a course (courseId from URL)
    Batch createBatch(Long courseId, Batch batch);

    // Get batch by ID
    Batch getBatchById(Long batchId);

    // Get all batches of a course
    List<Batch> getBatchesByCourseId(Long courseId);

    // Update batch (PUT = PATCH)
    Batch updateBatch(Long batchId, Batch batch);

    // Delete batch
    void deleteBatch(Long batchId);
}

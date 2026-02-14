package com.lms.management.service;

import java.util.List;

import com.lms.management.model.Batch;

public interface BatchService {

    Batch createBatch(Long courseId, Batch batch);

    Batch getBatchById(Long batchId);

    List<Batch> getBatchesByCourseId(Long courseId);

    Batch updateBatch(Long batchId, Batch batch);

    void deleteBatch(Long batchId);
}
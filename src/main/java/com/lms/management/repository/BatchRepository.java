package com.lms.management.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lms.management.model.Batch;

@Repository
public interface BatchRepository extends JpaRepository<Batch, Long> {

    // Get all batches for a course
    List<Batch> findByCourseId(Long courseId);

    // Get a specific batch of a course (if needed)
    Optional<Batch> findByCourseIdAndBatchId(Long courseId, Long batchId);

    // Used for validations (capacity, duplicates, etc.)
    boolean existsByCourseIdAndBatchName(Long courseId, String batchName);
    
    long countByCourseIdAndBatchNameStartingWith(
            Long courseId,
            String batchName
    );
}

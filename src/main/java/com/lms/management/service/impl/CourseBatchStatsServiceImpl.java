package com.lms.management.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.management.model.Batch;
import com.lms.management.model.CourseBatchStats;
import com.lms.management.repository.BatchRepository;
import com.lms.management.repository.CourseBatchStatsRepository;
import com.lms.management.service.CourseBatchStatsService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseBatchStatsServiceImpl implements CourseBatchStatsService {

    private final BatchRepository batchRepository;
    private final CourseBatchStatsRepository statsRepository;

    @Override
    public void recalculateStats(Long courseId) {

        List<Batch> batches = batchRepository.findByCourseId(courseId);

        int total = batches.size();
        int running = 0;
        int upcoming = 0;
        int completed = 0;

        for (Batch batch : batches) {
            if ("Running".equalsIgnoreCase(batch.getStatus())) {
                running++;
            } else if ("Upcoming".equalsIgnoreCase(batch.getStatus())) {
                upcoming++;
            } else if ("Completed".equalsIgnoreCase(batch.getStatus())) {
                completed++;
            }
        }

        CourseBatchStats stats =
                statsRepository.findById(courseId)
                        .orElse(new CourseBatchStats());

        stats.setCourseId(courseId);
        stats.setTotalBatches(total);
        stats.setRunningBatches(running);
        stats.setUpcomingBatches(upcoming);
        stats.setCompletedBatches(completed);

        // For now: parallel batches = running batches
        stats.setParallelBatches(running);

        // For now: 1 trainer per running batch
        stats.setRequiredTrainers(running);

        statsRepository.save(stats);
    }
}

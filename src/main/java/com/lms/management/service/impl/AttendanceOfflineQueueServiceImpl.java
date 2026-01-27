package com.lms.management.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.management.exception.ResourceNotFoundException;
import com.lms.management.model.AttendanceOfflineQueue;
import com.lms.management.model.AttendanceRecord;
import com.lms.management.repository.AttendanceOfflineQueueRepository;
import com.lms.management.repository.AttendanceRecordRepository;
import com.lms.management.service.AttendanceOfflineQueueService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AttendanceOfflineQueueServiceImpl
        implements AttendanceOfflineQueueService {

    private final AttendanceOfflineQueueRepository offlineQueueRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;

    // ===============================
    // STORE OFFLINE ATTENDANCE
    // ===============================
    @Override
    public AttendanceOfflineQueue save(AttendanceOfflineQueue queue) {
        queue.setQueuedAt(LocalDateTime.now());
        queue.setSynced(false);
        return offlineQueueRepository.save(queue);
    }

    // ===============================
    // VIEW OFFLINE QUEUE BY BATCH
    // ===============================
    @Override
    @Transactional(readOnly = true)
    public List<AttendanceOfflineQueue> getByBatch(Long batchId) {
        return offlineQueueRepository.findByBatchIdAndSyncedFalse(batchId);
    }

    // ===============================
    // SYNC OFFLINE → ATTENDANCE RECORD
    // ===============================
    @Override
    public void sync() {

        List<AttendanceOfflineQueue> pending =
                offlineQueueRepository.findBySyncedFalse();

        for (AttendanceOfflineQueue q : pending) {

            AttendanceRecord record = new AttendanceRecord();
            record.setAttendanceSessionId(q.getSessionId());
            record.setStudentId(q.getStudentId());
            record.setStatus(q.getStatus());
            record.setRemarks(q.getRemarks());
            record.setMarkedBy(0L); // system
            record.setSource("OFFLINE");

            attendanceRecordRepository.save(record);

            q.setSynced(true);
            offlineQueueRepository.save(q);
        }
    }

    // ===============================
    // DELETE OFFLINE QUEUE RECORD
    // ===============================
    @Override
    public void delete(Long id) {
        AttendanceOfflineQueue queue =
                offlineQueueRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("Offline queue not found")
                        );

        offlineQueueRepository.delete(queue);
    }
}

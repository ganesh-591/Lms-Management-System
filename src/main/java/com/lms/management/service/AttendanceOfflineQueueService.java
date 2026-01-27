package com.lms.management.service;

import java.util.List;

import com.lms.management.model.AttendanceOfflineQueue;

public interface AttendanceOfflineQueueService {

    AttendanceOfflineQueue save(AttendanceOfflineQueue queue);

    List<AttendanceOfflineQueue> getByBatch(Long batchId);

    void sync();

    void delete(Long id);
}

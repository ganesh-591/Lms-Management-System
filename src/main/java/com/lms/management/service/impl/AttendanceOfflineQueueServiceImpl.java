package com.lms.management.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.management.model.AttendanceOfflineQueue;
import com.lms.management.model.AttendanceRecord;
import com.lms.management.repository.AttendanceOfflineQueueRepository;
import com.lms.management.repository.AttendanceRecordRepository;
import com.lms.management.service.AttendanceOfflineQueueService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttendanceOfflineQueueServiceImpl implements AttendanceOfflineQueueService {

    private final AttendanceOfflineQueueRepository offlineQueueRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;

    @Override
    public List<AttendanceOfflineQueue> getPendingOfflineRecords() {
        return offlineQueueRepository.findBySyncedFalse();
    }

    @Override
    @Transactional
    public void syncOfflineAttendance(Long syncedByUserId) {

        List<AttendanceOfflineQueue> pendingRecords =
                offlineQueueRepository.findBySyncedFalse();

        for (AttendanceOfflineQueue offline : pendingRecords) {

            // ✅ Use existing repository method (NO repo change)
            Optional<AttendanceRecord> existingRecord =
                    attendanceRecordRepository
                            .findByAttendanceSessionIdAndStudentId(
                                    offline.getSessionId(),
                                    offline.getStudentId()
                            );

            // If already exists, mark offline record as synced and skip
            if (existingRecord.isPresent()) {
                offline.setSynced(true);
                continue;
            }

            AttendanceRecord record = new AttendanceRecord();
            record.setAttendanceSessionId(offline.getSessionId());
            record.setStudentId(offline.getStudentId());
            record.setStudentName(null); // resolved later from student_batch if needed
            record.setStatus(offline.getStatus());
            record.setRemarks(offline.getRemarks());
            record.setMarkedBy(syncedByUserId);
            record.setAttendanceDate(LocalDate.now());
            record.setSource("OFFLINE");

            attendanceRecordRepository.save(record);

            offline.setSynced(true);
        }

        offlineQueueRepository.saveAll(pendingRecords);
    }
}

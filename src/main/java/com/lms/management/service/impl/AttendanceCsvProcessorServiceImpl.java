package com.lms.management.service.impl;

import java.io.*;
import java.time.LocalDateTime;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.management.exception.ResourceNotFoundException;
import com.lms.management.model.AttendanceRecord;
import com.lms.management.model.AttendanceUploadJob;
import com.lms.management.repository.AttendanceRecordRepository;
import com.lms.management.repository.AttendanceUploadJobRepository;
import com.lms.management.repository.StudentBatchRepository;
import com.lms.management.service.AttendanceCsvProcessorService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AttendanceCsvProcessorServiceImpl
        implements AttendanceCsvProcessorService {

    private final AttendanceUploadJobRepository uploadJobRepository;
    private final AttendanceRecordRepository attendanceRecordRepository;
    private final StudentBatchRepository studentBatchRepository;

    @Override
    public void processUploadJob(Long uploadJobId) {

        AttendanceUploadJob job = uploadJobRepository.findById(uploadJobId)
                .orElseThrow(() -> new ResourceNotFoundException("Upload job not found"));

        File file = new File(job.getFilePath());

        try {
            if (file.getName().endsWith(".csv")) {
                processCsv(file, job);
            } else if (file.getName().endsWith(".xlsx")) {
                processExcel(file, job);
            }

            job.setStatus("PROCESSED");
            uploadJobRepository.save(job);

        } catch (Exception e) {
            job.setStatus("FAILED");
            uploadJobRepository.save(job);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    // ===============================
    // CSV PROCESSING
    // ===============================
    private void processCsv(File file, AttendanceUploadJob job) throws Exception {

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {

            String line;
            boolean skipHeader = true;

            while ((line = br.readLine()) != null) {
                if (skipHeader) {
                    skipHeader = false;
                    continue;
                }

                String[] data = line.split(",");

                Long studentId = Long.parseLong(data[0].trim());
                String status = data[1].trim();
                String remarks = data.length > 2 ? data[2].trim() : null;

                saveRecord(job, studentId, status, remarks);
            }
        }
    }

    // ===============================
    // EXCEL PROCESSING
    // ===============================
    private void processExcel(File file, AttendanceUploadJob job) throws Exception {

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Long studentId = (long) row.getCell(0).getNumericCellValue();
                String status = row.getCell(1).getStringCellValue();
                String remarks =
                        row.getCell(2) != null ? row.getCell(2).getStringCellValue() : null;

                saveRecord(job, studentId, status, remarks);
            }
        }
    }

    // ===============================
    // COMMON SAVE LOGIC
    // ===============================
    private void saveRecord(
            AttendanceUploadJob job,
            Long studentId,
            String status,
            String remarks
    ) {

        boolean enrolled =
                studentBatchRepository.existsByStudentIdAndBatchIdAndStatus(
                        studentId,
                        job.getBatchId(),
                        "ACTIVE"
                );

        if (!enrolled) return;

        AttendanceRecord record = new AttendanceRecord();
        record.setAttendanceSessionId(job.getSessionId());
        record.setStudentId(studentId);
        record.setStatus(status);
        record.setRemarks(remarks);
        record.setMarkedBy(job.getUploadedBy());
        record.setMarkedAt(LocalDateTime.now());
        record.setAttendanceDate(job.getAttendanceDate());
        record.setSource("FILE");

        attendanceRecordRepository.save(record);
    }
}

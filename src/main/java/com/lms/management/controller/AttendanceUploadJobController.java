package com.lms.management.controller;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.lms.management.model.AttendanceUploadJob;
import com.lms.management.service.AttendanceCsvProcessorService;
import com.lms.management.service.AttendanceUploadJobService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/attendance/upload-job")
@RequiredArgsConstructor
public class AttendanceUploadJobController {

    private final AttendanceUploadJobService attendanceUploadJobService;
    private final AttendanceCsvProcessorService attendanceCsvProcessorService;

    // ===============================
    // 1️⃣ UPLOAD FILE (CSV / EXCEL)
    // ===============================
    @PostMapping("/upload")
    @PreAuthorize("hasAuthority('ATTENDANCE_UPLOAD_CREATE')")
    public AttendanceUploadJob uploadFile(
            @RequestParam Long courseId,
            @RequestParam Long batchId,
            @RequestParam(required = false) Long sessionId,
            @RequestParam String attendanceDate,
            @RequestParam Long uploadedBy,
            @RequestPart MultipartFile file
    ) throws Exception {

        // ✅ Validate file type
        String filename = file.getOriginalFilename();
        if (filename == null ||
                !(filename.endsWith(".csv") || filename.endsWith(".xlsx"))) {
            throw new IllegalArgumentException("Only CSV or Excel files are allowed");
        }

        // ✅ FIX: Absolute project path (NOT Tomcat temp)
        String dirPath =
                System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "attendance";

        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // ✅ Save file
        String filePath =
                dirPath + File.separator + System.currentTimeMillis() + "_" + filename;

        File savedFile = new File(filePath);
        file.transferTo(savedFile);

        // ✅ Create upload job
        AttendanceUploadJob job = new AttendanceUploadJob();
        job.setCourseId(courseId);
        job.setBatchId(batchId);
        job.setSessionId(sessionId);
        job.setAttendanceDate(LocalDate.parse(attendanceDate));
        job.setFilePath(savedFile.getAbsolutePath()); // IMPORTANT
        job.setUploadedBy(uploadedBy);

        return attendanceUploadJobService.createJob(job);
    }

    // ===============================
    // 2️⃣ PROCESS FILE → RECORDS
    // ===============================
    @PostMapping("/{jobId}/process")
    @PreAuthorize("hasAuthority('ATTENDANCE_CSV_PROCESS')")
    public void processFile(@PathVariable Long jobId) {
        attendanceCsvProcessorService.processUploadJob(jobId);
    }

    // ===============================
    // VIEW JOBS
    // ===============================
    @GetMapping("/batch/{batchId}")
    public List<AttendanceUploadJob> getByBatch(@PathVariable Long batchId) {
        return attendanceUploadJobService.getByBatch(batchId);
    }

    @GetMapping("/status/{status}")
    public List<AttendanceUploadJob> getByStatus(@PathVariable String status) {
        return attendanceUploadJobService.getByStatus(status);
    }

    @GetMapping("/uploader/{userId}")
    public List<AttendanceUploadJob> getByUploader(@PathVariable Long userId) {
        return attendanceUploadJobService.getByUploader(userId);
    }
}

package com.lms.management.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.lms.management.service.AttendanceCsvProcessorService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/attendance/csv")
@RequiredArgsConstructor
public class AttendanceCsvProcessorController {

    private final AttendanceCsvProcessorService csvProcessorService;

    // ===============================
    // PROCESS CSV UPLOAD JOB
    // ===============================
    @PostMapping("/process/{uploadJobId}")
    @PreAuthorize("hasAuthority('ATTENDANCE_UPLOAD_PROCESS')")
    public void processCsv(
            @PathVariable Long uploadJobId
    ) {
        csvProcessorService.processUploadJob(uploadJobId);
    }
}

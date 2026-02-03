package com.lms.management.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lms.management.service.AttendanceSummaryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceSummaryController {

    private final AttendanceSummaryService attendanceSummaryService;

    @GetMapping("/summary")
    public Map<String, Object> getStudentAttendanceSummary(
            @RequestParam Long studentId,
            @RequestParam Long courseId,
            @RequestParam Long batchId
    ) {
        return attendanceSummaryService.getStudentEligibilitySummary(
                studentId, courseId, batchId
        );
    }
    @GetMapping("/batch-summary")
    public Map<String, Object> getBatchSummary(
            @RequestParam Long courseId,
            @RequestParam Long batchId
    ) {
        return attendanceSummaryService
                .getBatchSummary(courseId, batchId);
    }
   
}

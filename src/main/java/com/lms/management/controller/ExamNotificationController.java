package com.lms.management.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.lms.management.model.ExamNotification;
import com.lms.management.service.ExamNotificationService;

@RestController
@RequestMapping("/api/exams/{examId}/notification")
public class ExamNotificationController {

    private final ExamNotificationService examNotificationService;

    public ExamNotificationController(
            ExamNotificationService examNotificationService) {
        this.examNotificationService = examNotificationService;
    }

    // Create / update notification config
    @PostMapping
    @PreAuthorize("hasAuthority('EXAM_NOTIFICATION_UPDATE')")
    public ResponseEntity<ExamNotification> saveNotification(
            @PathVariable Long examId,
            @RequestBody ExamNotification notification) {

        return ResponseEntity.ok(
                examNotificationService.saveNotification(
                        examId, notification));
    }

    // Get notification config
    @GetMapping
    @PreAuthorize("hasAuthority('EXAM_NOTIFICATION_VIEW')")
    public ResponseEntity<ExamNotification> getNotification(
            @PathVariable Long examId) {

        return ResponseEntity.ok(
                examNotificationService.getNotificationByExamId(examId));
    }
}

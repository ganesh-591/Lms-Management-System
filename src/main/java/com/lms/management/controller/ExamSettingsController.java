package com.lms.management.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lms.management.model.ExamSettings;
import com.lms.management.service.ExamSettingsService;

@RestController
@RequestMapping("/api/exams/{examId}/settings")
public class ExamSettingsController {

    private final ExamSettingsService examSettingsService;

    public ExamSettingsController(ExamSettingsService examSettingsService) {
        this.examSettingsService = examSettingsService;
    }

    // Create / update exam settings (DRAFT only)
    @PostMapping
    public ResponseEntity<ExamSettings> saveSettings(
            @PathVariable Long examId,
            @RequestBody ExamSettings settings) {

        return ResponseEntity.ok(
                examSettingsService.saveSettings(examId, settings)
        );
    }

    // Get exam settings (read-only)
    @GetMapping
    public ResponseEntity<ExamSettings> getSettings(
            @PathVariable Long examId) {

        return ResponseEntity.ok(
                examSettingsService.getSettingsByExamId(examId)
        );
    }

    // 🔥 Toggle MCQ option shuffle
    @PatchMapping("/shuffle-options")
    public ResponseEntity<ExamSettings> updateShuffleOptions(
            @PathVariable Long examId,
            @RequestParam Boolean shuffle) {

        return ResponseEntity.ok(
                examSettingsService.updateShuffleOptions(examId, shuffle)
        );
    }
}

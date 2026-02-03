package com.lms.management.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.lms.management.model.ExamDesign;
import com.lms.management.service.ExamDesignService;

@RestController
@RequestMapping("/api/exams/{examId}/design")
public class ExamDesignController {

    private final ExamDesignService examDesignService;

    public ExamDesignController(ExamDesignService examDesignService) {
        this.examDesignService = examDesignService;
    }

    // Create or update exam design (DRAFT only)
    @PostMapping("/upload")
    @PreAuthorize("hasAuthority('EXAM_DESIGN_UPDATE')")
    public ResponseEntity<ExamDesign> uploadDesign(
            @PathVariable Long examId,
            @RequestParam String orientation,
            @RequestParam String watermarkType,
            @RequestParam String watermarkValue,
            @RequestParam Integer watermarkOpacity,
            @RequestParam(required = false) MultipartFile instituteLogo,
            @RequestParam(required = false) MultipartFile backgroundImage
    ) {
        return ResponseEntity.ok(
                examDesignService.uploadDesign(
                        examId,
                        orientation,
                        watermarkType,
                        watermarkValue,
                        watermarkOpacity,
                        instituteLogo,
                        backgroundImage
                )
        );
    }

    // Get exam design (read-only)
    @GetMapping
    @PreAuthorize("hasAuthority('EXAM_DESIGN_VIEW')")
    public ResponseEntity<ExamDesign> getDesign(@PathVariable Long examId) {
        return ResponseEntity.ok(
                examDesignService.getDesignByExamId(examId)
        );
    }
}

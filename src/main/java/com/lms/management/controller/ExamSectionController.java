package com.lms.management.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lms.management.model.ExamSection;
import com.lms.management.service.ExamSectionService;

@RestController
@RequestMapping("/api/exams/{examId}/sections")
public class ExamSectionController {

    private final ExamSectionService examSectionService;

    public ExamSectionController(
            ExamSectionService examSectionService) {
        this.examSectionService = examSectionService;
    }

    // Add section to exam
    @PostMapping
    public ResponseEntity<ExamSection> addSectionToExam(
            @PathVariable Long examId,
            @RequestParam Long sectionId,
            @RequestParam Integer sectionOrder,
            @RequestParam(required = false) Boolean shuffleQuestions) {

        return ResponseEntity.ok(
                examSectionService.addSectionToExam(
                        examId,
                        sectionId,
                        sectionOrder,
                        shuffleQuestions
                )
        );
    }

    // Get sections of exam
    @GetMapping
    public ResponseEntity<List<ExamSection>> getSectionsByExam(
            @PathVariable Long examId) {

        return ResponseEntity.ok(
                examSectionService.getSectionsByExam(examId)
        );
    }

    // 🔥 Toggle shuffle for section
    @PatchMapping("/{examSectionId}/shuffle")
    public ResponseEntity<ExamSection> updateShuffle(
            @PathVariable Long examId,
            @PathVariable Long examSectionId,
            @RequestParam Boolean shuffle) {

        return ResponseEntity.ok(
                examSectionService.updateShuffle(
                        examId,
                        examSectionId,
                        shuffle
                )
        );
    }

    // Remove section
    @DeleteMapping("/{examSectionId}")
    public ResponseEntity<Void> removeSection(
            @PathVariable Long examSectionId) {

        examSectionService.removeSectionFromExam(examSectionId);
        return ResponseEntity.noContent().build();
    }
}

package com.lms.management.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lms.management.model.QuestionSection;
import com.lms.management.service.QuestionSectionService;

@RestController
@RequestMapping("/api/sections")
public class QuestionSectionController {

    private final QuestionSectionService questionSectionService;

    public QuestionSectionController(
            QuestionSectionService questionSectionService) {
        this.questionSectionService = questionSectionService;
    }

    // ================= CREATE SECTION =================
    @PostMapping
    public ResponseEntity<QuestionSection> createSection(
            @RequestBody QuestionSection request) {

        return ResponseEntity.ok(
                questionSectionService.createSection(
                        request.getSectionName(),
                        request.getSectionDescription(),
                        request.getShuffleQuestions()
                )
        );
    }

    // ================= GET ALL SECTIONS =================
    @GetMapping
    public ResponseEntity<List<QuestionSection>> getAllSections() {
        return ResponseEntity.ok(
                questionSectionService.getAllSections()
        );
    }

    // ================= GET SECTION BY ID =================
    @GetMapping("/{sectionId}")
    public ResponseEntity<QuestionSection> getSection(
            @PathVariable Long sectionId) {

        return ResponseEntity.ok(
                questionSectionService.getSectionById(sectionId)
        );
    }

    // ================= DELETE SECTION =================
    @DeleteMapping("/{sectionId}")
    public ResponseEntity<Void> deleteSection(
            @PathVariable Long sectionId) {

        questionSectionService.deleteSection(sectionId);
        return ResponseEntity.noContent().build();
    }
}

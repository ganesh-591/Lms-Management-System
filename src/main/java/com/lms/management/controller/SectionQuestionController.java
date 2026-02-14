package com.lms.management.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lms.management.model.Question;
import com.lms.management.service.SectionQuestionService;

@RestController
@RequestMapping("/api/sections/{sectionId}/questions")
public class SectionQuestionController {

    private final SectionQuestionService sectionQuestionService;

    public SectionQuestionController(
            SectionQuestionService sectionQuestionService) {
        this.sectionQuestionService = sectionQuestionService;
    }

    // ================= ADD QUESTION TO SECTION =================
    // POST /api/sections/{sectionId}/questions/{questionId}
    @PostMapping("/{questionId}")
    @PreAuthorize("hasAuthority('SECTION_MANAGE')")
    public ResponseEntity<Void> addQuestionToSection(
            @PathVariable Long sectionId,
            @PathVariable Long questionId) {

        sectionQuestionService.addQuestionToSection(sectionId, questionId);
        return ResponseEntity.ok().build();
    }

    // ================= REMOVE QUESTION FROM SECTION =================
    // DELETE /api/sections/{sectionId}/questions/{questionId}
    @DeleteMapping("/{questionId}")
    @PreAuthorize("hasAuthority('SECTION_MANAGE')")
    public ResponseEntity<Void> removeQuestionFromSection(
            @PathVariable Long sectionId,
            @PathVariable Long questionId) {

        sectionQuestionService.removeQuestionFromSection(sectionId, questionId);
        return ResponseEntity.noContent().build();
    }

    // ================= GET QUESTIONS OF SECTION =================
    // GET /api/sections/{sectionId}/questions
    @GetMapping
    @PreAuthorize("hasAuthority('SECTION_VIEW')")
    public ResponseEntity<List<Question>> getQuestionsBySection(
            @PathVariable Long sectionId) {

        return ResponseEntity.ok(
                sectionQuestionService.getQuestionsBySection(sectionId)
        );
    }
}
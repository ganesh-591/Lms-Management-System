package com.lms.management.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.lms.management.model.QuestionOption;
import com.lms.management.service.QuestionOptionService;

@RestController
@RequestMapping("/api/questions/{questionId}/options")
public class QuestionOptionController {

    private final QuestionOptionService questionOptionService;

    public QuestionOptionController(
            QuestionOptionService questionOptionService) {
        this.questionOptionService = questionOptionService;
    }

    // Add options to a question
    @PostMapping
    @PreAuthorize("hasAuthority('QUESTION_OPTION_MANAGE')")
    public ResponseEntity<List<QuestionOption>> addOptions(
            @PathVariable Long questionId,
            @RequestBody List<QuestionOption> options) {

        return ResponseEntity.ok(
                questionOptionService.addOptions(
                        questionId, options));
    }

    // Get options of a question
    @GetMapping
    @PreAuthorize("hasAuthority('QUESTION_OPTION_VIEW')")
    public ResponseEntity<List<QuestionOption>> getOptions(
            @PathVariable Long questionId) {

        return ResponseEntity.ok(
                questionOptionService.getOptionsByQuestion(questionId));
    }

    // Delete an option
    @DeleteMapping("/{optionId}")
    @PreAuthorize("hasAuthority('QUESTION_OPTION_MANAGE')")
    public ResponseEntity<Void> deleteOption(
            @PathVariable Long optionId) {

        questionOptionService.deleteOption(optionId);
        return ResponseEntity.noContent().build();
    }
}

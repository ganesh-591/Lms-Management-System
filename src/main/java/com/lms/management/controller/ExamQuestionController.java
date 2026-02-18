package com.lms.management.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lms.management.model.ExamQuestion;
import com.lms.management.service.ExamQuestionService;

@RestController
@RequestMapping("/api/exam-sections/{examSectionId}/questions")
public class ExamQuestionController {

    private final ExamQuestionService examQuestionService;

    public ExamQuestionController(ExamQuestionService examQuestionService) {
        this.examQuestionService = examQuestionService;
    }

    // ================= ADD QUESTIONS (ADMIN) =================
    @PostMapping
    @PreAuthorize("hasAuthority('EXAM_QUESTION_MANAGE')")
    public ResponseEntity<List<ExamQuestion>> addQuestions(
            @PathVariable Long examSectionId,
            @RequestBody List<ExamQuestion> questions) {

        return ResponseEntity.ok(
                examQuestionService.addQuestions(examSectionId, questions)
        );
    }

    // ================= GET QUESTIONS =================
    @GetMapping
    @PreAuthorize("hasAuthority('EXAM_QUESTION_VIEW')")
    public ResponseEntity<List<Map<String, Object>>> getQuestions(
            @PathVariable Long examSectionId) {

        return ResponseEntity.ok(
                examQuestionService.getExamQuestionsForStudent(examSectionId)
        );
    }

    // ================= UPDATE MARKS / ORDER =================
    @PutMapping("/{examQuestionId}")
    public ResponseEntity<Void> updateQuestion(
            @PathVariable Long examSectionId,
            @PathVariable Long examQuestionId,
            @RequestBody ExamQuestion request) {

        examQuestionService.updateExamQuestion(
                examSectionId, examQuestionId, request);

        return ResponseEntity.noContent().build();
    }

    // ================= REMOVE QUESTION =================
    @DeleteMapping("/{examQuestionId}")
    @PreAuthorize("hasAuthority('EXAM_QUESTION_MANAGE')")
    public ResponseEntity<Void> removeQuestion(
            @PathVariable Long examQuestionId) {

        examQuestionService.removeExamQuestion(examQuestionId);
        return ResponseEntity.noContent().build();
    }
}
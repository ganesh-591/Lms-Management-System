package com.lms.management.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.lms.management.model.ExamQuestion;
import com.lms.management.service.ExamQuestionService;

@RestController
@RequestMapping("/api/exams/{examId}/questions")
public class ExamQuestionController {

    private final ExamQuestionService examQuestionService;

    public ExamQuestionController(
            ExamQuestionService examQuestionService) {
        this.examQuestionService = examQuestionService;
    }

    // Add questions to exam
    @PostMapping
    @PreAuthorize("hasAuthority('EXAM_QUESTION_MANAGE')")
    public ResponseEntity<List<ExamQuestion>> addQuestions(
            @PathVariable Long examId,
            @RequestBody List<ExamQuestion> questions) {

        return ResponseEntity.ok(
                examQuestionService.addQuestions(
                        examId, questions));
    }

    // Get questions of exam
    @GetMapping
    @PreAuthorize("hasAuthority('EXAM_QUESTION_VIEW')")
    public ResponseEntity<List<ExamQuestion>> getQuestions(
            @PathVariable Long examId) {

        return ResponseEntity.ok(
                examQuestionService.getQuestionsByExam(examId));
    }

    // Remove question from exam
    @DeleteMapping("/{examQuestionId}")
    @PreAuthorize("hasAuthority('EXAM_QUESTION_MANAGE')")
    public ResponseEntity<Void> removeQuestion(
            @PathVariable Long examQuestionId) {

        examQuestionService.removeExamQuestion(examQuestionId);
        return ResponseEntity.noContent().build();
    }
}

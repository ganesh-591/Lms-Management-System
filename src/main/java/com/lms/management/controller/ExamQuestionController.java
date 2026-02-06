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
    
    // ================= UPDATE MARKS / ORDER =================
    @PutMapping("/{examQuestionId}")
    @PreAuthorize("hasAuthority('EXAM_QUESTION_MANAGE')")
    public ResponseEntity<?> updateQuestion(
            @PathVariable Long examId,
            @PathVariable Long examQuestionId,
            @RequestBody ExamQuestion request) {

        return ResponseEntity.ok(
                examQuestionService.updateExamQuestion(
                        examId, examQuestionId, request)
        );
    }

    // Remove question from exam
    @DeleteMapping("/{examQuestionId}")
    @PreAuthorize("hasAuthority('EXAM_QUESTION_MANAGE')")
    public ResponseEntity<Void> removeQuestion(
            @PathVariable Long examQuestionId) {

        examQuestionService.removeExamQuestion(examQuestionId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/view")
    @PreAuthorize("hasAuthority('EXAM_ATTEMPT_START')")
    public ResponseEntity<List<Map<String, Object>>> getExamQuestionsForStudent(
            @PathVariable Long examId) {

        return ResponseEntity.ok(
                examQuestionService.getExamQuestionsForStudent(examId)
        );
    }  
}

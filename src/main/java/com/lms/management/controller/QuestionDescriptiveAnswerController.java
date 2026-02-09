package com.lms.management.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lms.management.service.ExamResponseService;
import com.lms.management.service.QuestionDescriptiveAnswerService;

@RestController
@RequestMapping("/api")
public class QuestionDescriptiveAnswerController {

    private final QuestionDescriptiveAnswerService service;
    private final ExamResponseService examResponseService; // ✅ ADD

    public QuestionDescriptiveAnswerController(
            QuestionDescriptiveAnswerService service,
            ExamResponseService examResponseService) { // ✅ ADD
        this.service = service;
        this.examResponseService = examResponseService;
    }

    // ================= QUESTION MODEL ANSWER =================

    @PostMapping("/questions/{questionId}/descriptive-answer")
    @PreAuthorize("hasAuthority('QUESTION_MANAGE')")
    public ResponseEntity<Map<String, Object>> save(
            @PathVariable Long questionId,
            @RequestBody Map<String, String> body) {

        return ResponseEntity.ok(
                service.createOrUpdate(
                        questionId,
                        body.get("answerText"),
                        body.get("guidelines")
                )
        );
    }

    @GetMapping("/questions/{questionId}/descriptive-answer")
    @PreAuthorize("hasAuthority('QUESTION_VIEW')")
    public ResponseEntity<Map<String, Object>> get(
            @PathVariable Long questionId) {

        return ResponseEntity.ok(
                service.getByQuestionId(questionId)
        );
    }

    @DeleteMapping("/questions/{questionId}/descriptive-answer")
    @PreAuthorize("hasAuthority('QUESTION_MANAGE')")
    public ResponseEntity<Void> delete(
            @PathVariable Long questionId) {

        service.deleteByQuestionId(questionId);
        return ResponseEntity.noContent().build();
    }

    // ================= ATTEMPT DESCRIPTIVE RESPONSES =================

    @GetMapping("/exam-attempts/{attemptId}/descriptive-responses")
    @PreAuthorize("hasAuthority('EXAM_RESPONSE_EVALUATE')")
    public ResponseEntity<List<Map<String, Object>>> getDescriptiveResponses(
            @PathVariable Long attemptId) {

        return ResponseEntity.ok(
                examResponseService
                        .getDescriptiveResponsesForEvaluation(attemptId)
        );
    }
}
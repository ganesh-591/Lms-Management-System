package com.lms.management.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lms.management.model.Question;
import com.lms.management.repository.QuestionRepository;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionRepository questionRepository;

    public QuestionController(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    // ================= CREATE QUESTION =================
    @PostMapping
    public ResponseEntity<Question> createQuestion(
            @RequestBody Question question) {

        return ResponseEntity.ok(
                questionRepository.save(question)
        );
    }
    
    @GetMapping
    public ResponseEntity<List<Question>> getAllQuestions() {
        return ResponseEntity.ok(
                questionRepository.findAll()
        );
    }

    // ================= GET QUESTION =================
    @GetMapping("/{questionId}")
    public ResponseEntity<Question> getQuestion(
            @PathVariable Long questionId) {

        return ResponseEntity.ok(
                questionRepository.findById(questionId)
                        .orElseThrow(() ->
                                new IllegalStateException("Question not found"))
        );
    }
    
    @PutMapping("/{questionId}")
    public ResponseEntity<Question> updateQuestion(
            @PathVariable Long questionId,
            @RequestBody Question request) {

        Question existing = questionRepository.findById(questionId)
                .orElseThrow(() ->
                        new IllegalStateException("Question not found"));

        existing.setQuestionText(request.getQuestionText());
        existing.setQuestionType(request.getQuestionType());

        return ResponseEntity.ok(
                questionRepository.save(existing)
        );
    }
    
    @DeleteMapping("/{questionId}")
    public ResponseEntity<Void> deleteQuestion(
            @PathVariable Long questionId) {

        questionRepository.deleteById(questionId);
        return ResponseEntity.noContent().build();
    }
    
    
}

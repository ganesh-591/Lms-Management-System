package com.lms.management.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.lms.management.model.Exam;
import com.lms.management.service.ExamService;

@RestController
@RequestMapping("/api/exams")
public class ExamController {

    private final ExamService examService;

    public ExamController(ExamService examService) {
        this.examService = examService;
    }

    /**
     * CREATE EXAM
     * Status will always be DRAFT
     */
    @PostMapping
    public ResponseEntity<Exam> createExam(@RequestBody Exam exam) {
        Exam createdExam = examService.createExam(exam);
        return new ResponseEntity<>(createdExam, HttpStatus.CREATED);
    }

    /**
     * PUBLISH EXAM
     * Only DRAFT → PUBLISHED allowed
     */
    @PutMapping("/{examId}/publish")
    public ResponseEntity<Exam> publishExam(@PathVariable Long examId) {
        Exam exam = examService.publishExam(examId);
        return ResponseEntity.ok(exam);
    }

    /**
     * CLOSE EXAM
     * Locks the exam permanently
     */
    @PutMapping("/{examId}/close")
    public ResponseEntity<Exam> closeExam(@PathVariable Long examId) {
        Exam exam = examService.closeExam(examId);
        return ResponseEntity.ok(exam);
    }

    /**
     * GET EXAM BY ID
     */
    @GetMapping("/{examId}")
    public ResponseEntity<Exam> getExamById(@PathVariable Long examId) {
        return ResponseEntity.ok(examService.getExamById(examId));
    }

    /**
     * GET EXAMS BY COURSE
     */
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<Exam>> getExamsByCourse(
            @PathVariable Long courseId) {
        return ResponseEntity.ok(examService.getExamsByCourseId(courseId));
    }

    /**
     * GET EXAMS BY BATCH
     */
    @GetMapping("/batch/{batchId}")
    public ResponseEntity<List<Exam>> getExamsByBatch(
            @PathVariable Long batchId) {
        return ResponseEntity.ok(examService.getExamsByBatchId(batchId));
    }
}

package com.lms.management.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
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

import com.lms.management.model.Exam;
import com.lms.management.service.ExamService;

@RestController
@RequestMapping("/api/exams")
public class ExamController {

    private final ExamService examService;

    public ExamController(ExamService examService) {
        this.examService = examService;
    }

    // CREATE
    @PostMapping
    @PreAuthorize("hasAuthority('EXAM_CREATE')")
    public ResponseEntity<Exam> createExam(@RequestBody Exam exam) {
        return new ResponseEntity<>(examService.createExam(exam), HttpStatus.CREATED);
    }

    // PUBLISH
    @PutMapping("/{examId}/publish")
    @PreAuthorize("hasAuthority('EXAM_PUBLISH')")
    public ResponseEntity<Exam> publishExam(@PathVariable Long examId) {
        return ResponseEntity.ok(examService.publishExam(examId));
    }

    // CLOSE
    @PutMapping("/{examId}/close")
    @PreAuthorize("hasAuthority('EXAM_CLOSE')")
    public ResponseEntity<Exam> closeExam(@PathVariable Long examId) {
        return ResponseEntity.ok(examService.closeExam(examId));
    }

    // GET
    @GetMapping("/{examId}")
    @PreAuthorize("hasAuthority('EXAM_VIEW')")
    public ResponseEntity<Exam> getExam(@PathVariable Long examId) {
        return ResponseEntity.ok(examService.getExamById(examId));
    }

    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAuthority('EXAM_VIEW')")
    public ResponseEntity<List<Exam>> getByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(examService.getExamsByCourseId(courseId));
    }

    @GetMapping("/batch/{batchId}")
    @PreAuthorize("hasAuthority('EXAM_VIEW')")
    public ResponseEntity<List<Exam>> getByBatch(@PathVariable Long batchId) {
        return ResponseEntity.ok(examService.getExamsByBatchId(batchId));
    }
    
    @GetMapping
    @PreAuthorize("hasAuthority('EXAM_VIEW')")
    public ResponseEntity<List<Exam>> getAllExams() {
        return ResponseEntity.ok(
                examService.getAllExams()
        );
    }
    // ============ DELETE APIs ============

    // SOFT DELETE
    @DeleteMapping("/{examId}")
    @PreAuthorize("hasAuthority('EXAM_DELETE')")
    public ResponseEntity<Void> softDelete(@PathVariable Long examId) {
        examService.softDeleteExam(examId);
        return ResponseEntity.noContent().build();
    }

    // RESTORE
    @PutMapping("/{examId}/restore")
    @PreAuthorize("hasAuthority('EXAM_RESTORE')")
    public ResponseEntity<Void> restore(@PathVariable Long examId) {
        examService.restoreExam(examId);
        return ResponseEntity.noContent().build();
    }

    // HARD DELETE
    @DeleteMapping("/{examId}/hard")
    @PreAuthorize("hasAuthority('EXAM_HARD_DELETE')")
    public ResponseEntity<Void> hardDelete(@PathVariable Long examId) {
        examService.hardDeleteExam(examId);
        return ResponseEntity.noContent().build();
    }
}

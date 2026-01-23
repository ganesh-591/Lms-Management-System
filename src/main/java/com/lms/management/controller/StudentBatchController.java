package com.lms.management.controller;

import java.util.List;

import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.lms.management.exception.UnauthorizedAccessException;
import com.lms.management.model.StudentBatch;
import com.lms.management.service.StudentBatchService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/student-batches")
@RequiredArgsConstructor
public class StudentBatchController {

    private final StudentBatchService studentBatchService;

    // ================= ENROLL =================
    @PostMapping("/enroll")
    public ResponseEntity<StudentBatch> enrollStudent(
            @RequestParam Long studentId,
            @RequestParam String studentName,
            @RequestParam Long courseId,
            @RequestParam Long batchId) {

        requirePermission("STUDENT_BATCH_CREATE");

        return new ResponseEntity<>(
                studentBatchService.enrollStudent(
                        studentId, studentName, courseId, batchId),
                HttpStatus.CREATED
        );
    }

    // ================= UPDATE (PUT = PATCH) =================
    @PutMapping("/{studentBatchId}")
    public ResponseEntity<StudentBatch> updateEnrollment(
            @PathVariable Long studentBatchId,
            @RequestBody StudentBatch updated) {

        requirePermission("STUDENT_BATCH_UPDATE");

        return ResponseEntity.ok(
                studentBatchService.updateEnrollment(studentBatchId, updated)
        );
    }

    // ================= VIEW STUDENTS BY BATCH =================
    @GetMapping("/batch/{batchId}")
    public ResponseEntity<List<StudentBatch>> getStudentsByBatch(
            @PathVariable Long batchId) {

        requirePermission("STUDENT_BATCH_VIEW");

        return ResponseEntity.ok(
                studentBatchService.getStudentsByBatch(batchId)
        );
    }

    // ================= VIEW OWN BATCH =================
    @GetMapping("/student/{studentId}")
    public ResponseEntity<StudentBatch> getStudentBatch(
            @PathVariable Long studentId) {

        requirePermission("STUDENT_BATCH_SELF_VIEW");

        return ResponseEntity.ok(
                studentBatchService.getStudentCurrentBatch(studentId)
        );
    }

    // ================= REMOVE =================
    @DeleteMapping("/{studentBatchId}")
    public ResponseEntity<Void> removeStudent(
            @PathVariable Long studentBatchId) {

        requirePermission("STUDENT_BATCH_DELETE");

        studentBatchService.removeStudent(studentBatchId);
        return ResponseEntity.noContent().build();
    }

    // ================= PERMISSION CHECK =================
    private void requirePermission(String permission) {

        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getAuthorities().stream()
                .noneMatch(a -> a.getAuthority().equals(permission))) {

            throw new UnauthorizedAccessException(
                    "Missing permission: " + permission
            );
        }
    }
}

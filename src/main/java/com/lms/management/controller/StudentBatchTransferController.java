package com.lms.management.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.lms.management.exception.UnauthorizedAccessException;
import com.lms.management.model.StudentBatchTransfer;
import com.lms.management.service.StudentBatchTransferService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/student-batch-transfers")
@RequiredArgsConstructor
public class StudentBatchTransferController {

    private final StudentBatchTransferService transferService;

    // ================= TRANSFER =================
    @PostMapping("/transfer")
    public ResponseEntity<StudentBatchTransfer> transferStudent(
            @RequestParam Long studentId,
            @RequestParam Long courseId,
            @RequestParam Long toBatchId,
            @RequestParam String reason) {

        requirePermission("STUDENT_BATCH_TRANSFER_CREATE");

        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        String transferredBy = auth.getName(); // admin username / id

        StudentBatchTransfer transfer =
                transferService.transferStudent(
                        studentId,
                        courseId,
                        toBatchId,
                        reason,
                        transferredBy
                );

        return new ResponseEntity<>(transfer, HttpStatus.CREATED);
    }

    // ================= VIEW BY STUDENT =================
    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<StudentBatchTransfer>> getTransfersByStudent(
            @PathVariable Long studentId) {

        requirePermission("STUDENT_BATCH_TRANSFER_VIEW");

        return ResponseEntity.ok(
                transferService.getTransfersByStudent(studentId)
        );
    }

    // ================= VIEW BY COURSE =================
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<StudentBatchTransfer>> getTransfersByCourse(
            @PathVariable Long courseId) {

        requirePermission("STUDENT_BATCH_TRANSFER_VIEW");

        return ResponseEntity.ok(
                transferService.getTransfersByCourse(courseId)
        );
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

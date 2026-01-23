package com.lms.management.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.lms.management.exception.UnauthorizedAccessException;
import com.lms.management.model.Batch;
import com.lms.management.service.BatchService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/batches")
@RequiredArgsConstructor
public class BatchController {

    private final BatchService batchService;

    // ================= CREATE =================
    @PostMapping("/course/{courseId}")
    public ResponseEntity<Batch> createBatch(
            @PathVariable Long courseId,
            @RequestBody Batch batch) {

        requirePermission("BATCH_CREATE");

        Batch createdBatch = batchService.createBatch(courseId, batch);
        return new ResponseEntity<>(createdBatch, HttpStatus.CREATED);
    }

    // ================= GET BY ID =================
    @GetMapping("/{batchId}")
    public ResponseEntity<Batch> getBatchById(@PathVariable Long batchId) {

        requirePermission("BATCH_VIEW");

        return ResponseEntity.ok(batchService.getBatchById(batchId));
    }

    // ================= GET BY COURSE =================
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<Batch>> getBatchesByCourseId(
            @PathVariable Long courseId) {

        requirePermission("BATCH_VIEW");

        return ResponseEntity.ok(batchService.getBatchesByCourseId(courseId));
    }

    // ================= UPDATE (PUT = PATCH) =================
    @PutMapping("/{batchId}")
    public ResponseEntity<Batch> updateBatch(
            @PathVariable Long batchId,
            @RequestBody Batch batch) {

        requirePermission("BATCH_UPDATE");

        return ResponseEntity.ok(batchService.updateBatch(batchId, batch));
    }

    // ================= DELETE =================
    @DeleteMapping("/{batchId}")
    public ResponseEntity<Void> deleteBatch(@PathVariable Long batchId) {

        requirePermission("BATCH_DELETE");

        batchService.deleteBatch(batchId);
        return ResponseEntity.noContent().build();
    }

    // ================= PERMISSION CHECK =================
    private void requirePermission(String permission) {

        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (auth == null || auth.getAuthorities().stream()
                .noneMatch(a -> a.getAuthority().equals(permission))) {

            throw new UnauthorizedAccessException(
                    "Missing permission: " + permission
            );
        }
    }
}

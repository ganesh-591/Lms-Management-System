package com.lms.management.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.lms.management.exception.ResourceNotFoundException;
import com.lms.management.exception.UnauthorizedAccessException;
import com.lms.management.model.CourseBatchStats;
import com.lms.management.repository.CourseBatchStatsRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/course-batch-stats")
@RequiredArgsConstructor
public class CourseBatchStatsController {

    private final CourseBatchStatsRepository statsRepository;

    // ================= GET BY COURSE =================
    @GetMapping("/course/{courseId}")
    public ResponseEntity<CourseBatchStats> getStatsByCourse(
            @PathVariable Long courseId) {

        requirePermission("COURSE_BATCH_STATS_VIEW");

        CourseBatchStats stats = statsRepository.findById(courseId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Batch stats not found for course"));

        return ResponseEntity.ok(stats);
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

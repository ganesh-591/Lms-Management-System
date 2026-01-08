package com.lms.management.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.lms.management.exception.UnauthorizedAccessException;
import com.lms.management.model.Course;
import com.lms.management.service.CourseService;

@RestController
@RequestMapping("/api/courses")
@CrossOrigin
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    // ================= CREATE =================
    @PostMapping
    public ResponseEntity<Course> createCourse(@RequestBody Course course) {

        requirePermission("COURSE_CREATE");

        Course created = courseService.createCourse(course);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    // ================= GET ALL =================
    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses() {

        requirePermission("COURSE_VIEW");

        return ResponseEntity.ok(courseService.getAllCourses());
    }

    // ================= GET BY ID =================
    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourse(@PathVariable Long id) {

        requirePermission("COURSE_VIEW");

        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    // ================= UPDATE =================
    @PutMapping("/{id}")
    public ResponseEntity<Course> updateCourse(
            @PathVariable Long id,
            @RequestBody Course course) {

        requirePermission("COURSE_UPDATE");

        return ResponseEntity.ok(courseService.updateCourse(id, course));
    }

    // ================= DELETE =================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {

        requirePermission("COURSE_DELETE");

        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }

    // ================= PERMISSION CHECK =================
    private void requirePermission(String permission) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null) {
            throw new UnauthorizedAccessException("Authentication required");
        }

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return; // ADMIN has full access
        }

        boolean hasPermission = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(permission));

        if (!hasPermission) {
            throw new UnauthorizedAccessException(
                    "Missing permission: " + permission
            );
        }
    }
}

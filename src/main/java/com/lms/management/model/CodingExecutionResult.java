package com.lms.management.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "coding_execution_result")
@Getter
@Setter
public class CodingExecutionResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long executionId;

    @Column(nullable = false)
    private Long responseId;

    @Column(nullable = false)
    private Long testCaseId;

    @Column(columnDefinition = "TEXT")
    private String actualOutput;

    @Column(nullable = false)
    private Boolean passed;

    // ✅ STEP 2 ADDITIONS
    @Column(nullable = false)
    private Long executionTimeMs;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;
}

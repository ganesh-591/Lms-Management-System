package com.lms.management.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
        name = "coding_execution_result",
        indexes = {
            @Index(name = "idx_response", columnList = "responseId"),
            @Index(name = "idx_testcase", columnList = "testCaseId")
        }
)
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

    // ✅ NEW FIELD (Enterprise Upgrade)
    @Column(name = "execution_status", nullable = false, length = 20)
    private String executionStatus;

    @Column
    private Long executionTimeMs;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;
}

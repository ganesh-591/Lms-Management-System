package com.lms.management.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "coding_test_case")
@Getter
@Setter
public class CodingTestCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long testCaseId;

    @Column(nullable = false)
    private Long questionId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String inputData;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String expectedOutput;

    @Column(nullable = false)
    private Boolean hidden;
}

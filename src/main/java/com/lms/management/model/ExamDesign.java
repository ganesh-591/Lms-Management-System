package com.lms.management.model;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "exam_design",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "exam_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
public class ExamDesign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "design_id")
    private Long designId;

    @Column(name = "exam_id", nullable = false)
    private Long examId;

    // PORTRAIT / LANDSCAPE
    @Column(name = "orientation", nullable = false)
    private String orientation;

    @Column(name = "institute_logo_path")
    private String instituteLogoPath;

    @Column(name = "background_image_path")
    private String backgroundImagePath;

    // IMAGE / TEXT
    @Column(name = "watermark_type")
    private String watermarkType;

    @Column(name = "watermark_value")
    private String watermarkValue;

    // 0 – 100
    @Column(name = "watermark_opacity")
    private Integer watermarkOpacity;
}

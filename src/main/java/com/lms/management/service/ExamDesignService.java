package com.lms.management.service;

import org.springframework.web.multipart.MultipartFile;

import com.lms.management.model.ExamDesign;

public interface ExamDesignService {

    ExamDesign uploadDesign(
            Long examId,
            String orientation,
            String watermarkType,
            String watermarkValue,
            Integer watermarkOpacity,
            MultipartFile instituteLogo,
            MultipartFile backgroundImage
    );

    ExamDesign getDesignByExamId(Long examId);
}

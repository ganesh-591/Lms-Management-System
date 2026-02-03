package com.lms.management.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.lms.management.model.Exam;
import com.lms.management.model.ExamDesign;
import com.lms.management.repository.ExamDesignRepository;
import com.lms.management.repository.ExamRepository;
import com.lms.management.service.ExamDesignService;
import com.lms.management.util.FileUploadUtil;

@Service
@Transactional
public class ExamDesignServiceImpl implements ExamDesignService {

    private final ExamDesignRepository examDesignRepository;
    private final ExamRepository examRepository;

    public ExamDesignServiceImpl(
            ExamDesignRepository examDesignRepository,
            ExamRepository examRepository) {
        this.examDesignRepository = examDesignRepository;
        this.examRepository = examRepository;
    }

    // ✅ ADDED @Override (required)
    @Override
    public ExamDesign uploadDesign(
            Long examId,
            String orientation,
            String watermarkType,
            String watermarkValue,
            Integer watermarkOpacity,
            MultipartFile instituteLogo,
            MultipartFile backgroundImage
    ) {

        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new IllegalStateException("Exam not found"));

        if (!"DRAFT".equals(exam.getStatus())) {
            throw new IllegalStateException("Design can be changed only in DRAFT");
        }

        ExamDesign design = examDesignRepository
                .findByExamId(examId)
                .orElse(new ExamDesign());

        design.setExamId(examId);
        design.setOrientation(orientation);
        design.setWatermarkType(watermarkType);
        design.setWatermarkValue(watermarkValue);
        design.setWatermarkOpacity(watermarkOpacity);

        if (instituteLogo != null && !instituteLogo.isEmpty()) {
            String logoPath = FileUploadUtil.saveFile(instituteLogo, "exam-logos");
            design.setInstituteLogoPath(logoPath);
        }

        if (backgroundImage != null && !backgroundImage.isEmpty()) {
            String bgPath = FileUploadUtil.saveFile(backgroundImage, "exam-backgrounds");
            design.setBackgroundImagePath(bgPath);
        }

        return examDesignRepository.save(design);
    }

    // ✅ ADDED (missing interface method)
    @Override
    public ExamDesign getDesignByExamId(Long examId) {
        return examDesignRepository.findByExamId(examId)
                .orElseThrow(() ->
                        new IllegalStateException("Exam design not found"));
    }
}

package com.lms.management.service;

import java.util.List;

import com.lms.management.model.ExamSection;

public interface ExamSectionService {

    ExamSection addSectionToExam(
            Long examId,
            Long sectionId,
            Integer sectionOrder,
            Boolean shuffleQuestions
    );

    List<ExamSection> getSectionsByExam(Long examId);

    void removeSectionFromExam(Long examSectionId);

    // 🔥 Toggle shuffle for section
    ExamSection updateShuffle(
            Long examId,
            Long examSectionId,
            Boolean shuffle
    );
}

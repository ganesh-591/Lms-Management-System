package com.lms.management.service;

import java.util.List;

import com.lms.management.model.QuestionSection;

public interface QuestionSectionService {

    // Create section (MCQ / CODING / DESCRIPTIVE block)
    QuestionSection createSection(
            String sectionName,
            String description,
            Boolean shuffleQuestions
    );

    // Get all sections
    List<QuestionSection> getAllSections();

    // Get single section
    QuestionSection getSectionById(Long sectionId);

    // Delete section
    void deleteSection(Long sectionId);
}
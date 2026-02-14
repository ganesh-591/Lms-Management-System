package com.lms.management.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.management.model.QuestionSection;
import com.lms.management.repository.QuestionSectionRepository;
import com.lms.management.service.QuestionSectionService;

@Service
@Transactional
public class QuestionSectionServiceImpl implements QuestionSectionService {

    private final QuestionSectionRepository questionSectionRepository;

    public QuestionSectionServiceImpl(
            QuestionSectionRepository questionSectionRepository) {
        this.questionSectionRepository = questionSectionRepository;
    }

    @Override
    public QuestionSection createSection(
            String sectionName,
            String description,
            Boolean shuffleQuestions) {

        QuestionSection section = new QuestionSection();
        section.setSectionName(sectionName);
        section.setSectionDescription(description);
        section.setShuffleQuestions(
                shuffleQuestions != null ? shuffleQuestions : false
        );

        return questionSectionRepository.save(section);
    }

    @Override
    public List<QuestionSection> getAllSections() {
        return questionSectionRepository.findAll();
    }

    @Override
    public QuestionSection getSectionById(Long sectionId) {
        return questionSectionRepository.findById(sectionId)
                .orElseThrow(() ->
                        new IllegalStateException("Section not found"));
    }

    @Override
    public void deleteSection(Long sectionId) {
        questionSectionRepository.deleteById(sectionId);
    }
}
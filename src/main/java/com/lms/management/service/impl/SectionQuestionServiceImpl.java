package com.lms.management.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.management.model.Question;
import com.lms.management.model.QuestionSection;
import com.lms.management.model.SectionQuestion;
import com.lms.management.repository.QuestionRepository;
import com.lms.management.repository.QuestionSectionRepository;
import com.lms.management.repository.SectionQuestionRepository;
import com.lms.management.service.SectionQuestionService;

@Service
@Transactional
public class SectionQuestionServiceImpl implements SectionQuestionService {

    private final SectionQuestionRepository sectionQuestionRepository;
    private final QuestionSectionRepository questionSectionRepository;
    private final QuestionRepository questionRepository;

    public SectionQuestionServiceImpl(
            SectionQuestionRepository sectionQuestionRepository,
            QuestionSectionRepository questionSectionRepository,
            QuestionRepository questionRepository) {

        this.sectionQuestionRepository = sectionQuestionRepository;
        this.questionSectionRepository = questionSectionRepository;
        this.questionRepository = questionRepository;
    }

    @Override
    public void addQuestionToSection(Long sectionId, Long questionId) {

        QuestionSection section = questionSectionRepository.findById(sectionId)
                .orElseThrow(() -> new IllegalStateException("Section not found"));

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalStateException("Question not found"));

        SectionQuestion mapping = new SectionQuestion();
        mapping.setSectionId(section.getSectionId());
        mapping.setQuestionId(question.getQuestionId());

        sectionQuestionRepository.save(mapping);
    }

    @Override
    public void removeQuestionFromSection(Long sectionId, Long questionId) {

        SectionQuestion mapping =
                sectionQuestionRepository
                        .findBySectionIdAndQuestionId(sectionId, questionId)
                        .orElseThrow(() -> new IllegalStateException("Mapping not found"));

        sectionQuestionRepository.delete(mapping);
    }

    @Override
    public List<Question> getQuestionsBySection(Long sectionId) {

        return sectionQuestionRepository.findQuestionsBySectionId(sectionId);
    }
    
    
}
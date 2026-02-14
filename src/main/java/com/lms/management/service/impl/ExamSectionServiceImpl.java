package com.lms.management.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.management.model.ExamSection;
import com.lms.management.repository.ExamSectionRepository;
import com.lms.management.service.ExamSectionService;

@Service
@Transactional
public class ExamSectionServiceImpl implements ExamSectionService {

    private final ExamSectionRepository examSectionRepository;

    public ExamSectionServiceImpl(
            ExamSectionRepository examSectionRepository) {
        this.examSectionRepository = examSectionRepository;
    }

    @Override
    public ExamSection addSectionToExam(
            Long examId,
            Long sectionId,
            Integer sectionOrder,
            Boolean shuffleQuestions) {

        ExamSection examSection = new ExamSection();
        examSection.setExamId(examId);
        examSection.setSectionId(sectionId);
        examSection.setSectionOrder(sectionOrder);
        examSection.setShuffleQuestions(
                shuffleQuestions != null ? shuffleQuestions : false
        );

        return examSectionRepository.save(examSection);
    }

    @Override
    public List<ExamSection> getSectionsByExam(Long examId) {
    	return examSectionRepository.findByExamIdOrderBySectionOrderAsc(examId);
    }

    @Override
    public void removeSectionFromExam(Long examSectionId) {
        examSectionRepository.deleteById(examSectionId);
    }
}
package com.lms.management.service;

import java.util.List;
import java.util.Map;

import com.lms.management.model.ExamQuestion;

public interface ExamQuestionService {

    List<ExamQuestion> addQuestions(
            Long examSectionId, List<ExamQuestion> questions);

    List<ExamQuestion> getQuestionsBySection(Long examSectionId);

    ExamQuestion updateExamQuestion(
            Long examSectionId,
            Long examQuestionId,
            ExamQuestion request);

    void removeExamQuestion(Long examQuestionId);
    
    List<Map<String, Object>> getExamQuestionsForStudent(Long examId);
}
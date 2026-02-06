package com.lms.management.service;

import java.util.List;
import java.util.Map;

import com.lms.management.model.ExamQuestion;

public interface ExamQuestionService {

    List<ExamQuestion> addQuestions(
            Long examId, List<ExamQuestion> questions);

    List<ExamQuestion> getQuestionsByExam(Long examId);

    ExamQuestion updateExamQuestion(
            Long examId,
            Long examQuestionId,
            ExamQuestion request);

    void removeExamQuestion(Long examQuestionId);
    
    List<Map<String, Object>> getExamQuestionsForStudent(Long examId);
}

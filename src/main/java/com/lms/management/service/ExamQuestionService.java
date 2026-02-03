package com.lms.management.service;

import java.util.List;

import com.lms.management.model.ExamQuestion;

public interface ExamQuestionService {

    List<ExamQuestion> addQuestions(
            Long examId, List<ExamQuestion> questions);

    List<ExamQuestion> getQuestionsByExam(Long examId);

    void removeExamQuestion(Long examQuestionId);
}

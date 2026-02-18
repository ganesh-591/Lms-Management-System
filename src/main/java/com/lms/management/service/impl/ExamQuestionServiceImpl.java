package com.lms.management.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.management.model.ExamQuestion;
import com.lms.management.model.ExamResponse;
import com.lms.management.model.Question;
import com.lms.management.repository.ExamQuestionRepository;
import com.lms.management.repository.ExamResponseRepository;
import com.lms.management.repository.ExamSettingsRepository;
import com.lms.management.repository.QuestionOptionRepository;
import com.lms.management.repository.QuestionRepository;
import com.lms.management.service.ExamQuestionService;

@Service
@Transactional
public class ExamQuestionServiceImpl implements ExamQuestionService {

    private final ExamQuestionRepository examQuestionRepository;
    private final QuestionRepository questionRepository;
    private final QuestionOptionRepository questionOptionRepository;
    private final ExamResponseRepository examResponseRepository;
    private final ExamSettingsRepository examSettingsRepository;
    

    public ExamQuestionServiceImpl(
            ExamQuestionRepository examQuestionRepository,
            QuestionRepository questionRepository,
            QuestionOptionRepository questionOptionRepository,
            ExamResponseRepository examResponseRepository,
            ExamSettingsRepository examSettingsRepository
    ) {
        this.examQuestionRepository = examQuestionRepository;
        this.questionRepository = questionRepository;
        this.questionOptionRepository = questionOptionRepository;
        this.examResponseRepository = examResponseRepository;
        this.examSettingsRepository = examSettingsRepository;
    }

    // ================= ADD QUESTIONS TO EXAM SECTION =================
    @Override
    public List<ExamQuestion> addQuestions(
            Long examSectionId,
            List<ExamQuestion> questions) {

        for (ExamQuestion q : questions) {

            if (examQuestionRepository
                    .existsByExamSectionIdAndQuestionId(
                            examSectionId,
                            q.getQuestionId())) {

                throw new IllegalStateException(
                        "Duplicate question inside section");
            }

            q.setExamSectionId(examSectionId);
        }

        return examQuestionRepository.saveAll(questions);
    }

    // ================= GET QUESTIONS BY SECTION (ADMIN) =================
    @Override
    public List<ExamQuestion> getQuestionsBySection(Long examSectionId) {

        return examQuestionRepository
                .findByExamSectionIdOrderByQuestionOrderAsc(examSectionId);
    }

    // ================= UPDATE EXAM QUESTION =================
    @Override
    public ExamQuestion updateExamQuestion(
            Long examSectionId,
            Long examQuestionId,
            ExamQuestion request) {

        ExamQuestion existing =
                examQuestionRepository.findById(examQuestionId)
                        .orElseThrow(() ->
                                new IllegalStateException("Exam question not found"));

        if (!existing.getExamSectionId().equals(examSectionId)) {
            throw new IllegalStateException("Invalid section mapping");
        }

        existing.setMarks(request.getMarks());
        existing.setQuestionOrder(request.getQuestionOrder());

        return examQuestionRepository.save(existing);
    }

    // ================= REMOVE QUESTION FROM SECTION =================
    @Override
    public void removeExamQuestion(Long examQuestionId) {
        examQuestionRepository.deleteById(examQuestionId);
    }

    // ================= GET QUESTIONS FOR STUDENT (PER ATTEMPT) =================
    @Override
    public List<Map<String, Object>> getExamQuestionsForStudent(Long attemptId) {

        // 🔥 FETCH FROM exam_response TO PRESERVE SHUFFLE ORDER
        List<ExamResponse> responses =
                examResponseRepository
                        .findByAttemptIdOrderByResponseIdAsc(attemptId);

        if (responses.isEmpty()) {
            throw new IllegalStateException("No questions found for this attempt");
        }

        List<Map<String, Object>> result = new ArrayList<>();

        for (ExamResponse response : responses) {

            ExamQuestion eq =
                    examQuestionRepository
                            .findById(response.getExamQuestionId())
                            .orElseThrow();

            Question question =
                    questionRepository
                            .findById(eq.getQuestionId())
                            .orElseThrow();

            Map<String, Object> q = new HashMap<>();
            q.put("examQuestionId", eq.getExamQuestionId());
            q.put("questionId", question.getQuestionId());
            q.put("questionText", question.getQuestionText());
            q.put("questionType", question.getQuestionType());
            q.put("marks", eq.getMarks());

            // ================= MCQ OPTIONS =================
            if ("MCQ".equalsIgnoreCase(question.getQuestionType())) {

                List<Map<String, Object>> options =
                        questionOptionRepository
                                .findByQuestionId(question.getQuestionId())
                                .stream()
                                .map(opt -> {
                                    Map<String, Object> option = new HashMap<>();
                                    option.put("optionId", opt.getOptionId());
                                    option.put("optionText", opt.getOptionText());
                                    return option;
                                })
                                .toList();

                Boolean shuffleOptions =
                        examSettingsRepository
                                .findByExamId(
                                        examQuestionRepository
                                                .findById(eq.getExamQuestionId())
                                                .orElseThrow()
                                                .getExamSectionId()
                                )
                                .map(settings -> settings.getShuffleOptions())
                                .orElse(false);

                if (Boolean.TRUE.equals(shuffleOptions)) {
                    options = new ArrayList<>(options);
                    Collections.shuffle(options);
                }

                q.put("options", options);
            }

            result.add(q);
        }

        return result;
    }
}

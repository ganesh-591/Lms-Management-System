package com.lms.management.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.lms.management.model.ContentType;
import com.lms.management.model.Question;
import com.lms.management.model.QuestionDescriptiveAnswer;
import com.lms.management.repository.QuestionDescriptiveAnswerRepository;
import com.lms.management.repository.QuestionRepository;
import com.lms.management.util.FileUploadUtil;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionRepository questionRepository;
    private final QuestionDescriptiveAnswerRepository descriptiveRepository;

    public QuestionController(
            QuestionRepository questionRepository,
            QuestionDescriptiveAnswerRepository descriptiveRepository) {

        this.questionRepository = questionRepository;
        this.descriptiveRepository = descriptiveRepository;
    }

    // ================= CREATE QUESTION (JSON - TEXT / CODING / DESCRIPTIVE) =================
    @PostMapping(
            consumes = "application/json",
            produces = "application/json"
    )
    public ResponseEntity<Question> createQuestionJson(
            @RequestBody Map<String, Object> request
    ) {

        String questionText = (String) request.get("questionText");
        String questionType = (String) request.get("questionType");
        String contentTypeStr = (String) request.get("contentType");

        Question question = new Question();
        question.setQuestionText(questionText);
        question.setQuestionType(questionType);
        question.setContentType(ContentType.valueOf(contentTypeStr));

        Question savedQuestion = questionRepository.save(question);

        // ================= DESCRIPTIVE EXTRA SAVE =================
        if ("DESCRIPTIVE".equals(questionType)) {

            String modelAnswer = (String) request.get("modelAnswer");
            String keywords = (String) request.get("keywords");

            if (modelAnswer != null && !modelAnswer.isBlank()) {

                QuestionDescriptiveAnswer descriptive =
                        new QuestionDescriptiveAnswer();

                descriptive.setQuestionId(savedQuestion.getQuestionId());
                descriptive.setAnswerText(modelAnswer);
                descriptive.setGuidelines(keywords);
                descriptive.setCreatedAt(LocalDateTime.now());

                descriptiveRepository.save(descriptive);
            }
        }

        return ResponseEntity.ok(savedQuestion);
    }

    // ================= CREATE QUESTION (MULTIPART - IMAGE SUPPORT) =================
    @PostMapping(
            consumes = "multipart/form-data",
            produces = "application/json"
    )
    public ResponseEntity<Question> createQuestionMultipart(

            @RequestParam(required = false) String questionText,
            @RequestParam String questionType,
            @RequestParam ContentType contentType,
            @RequestParam(required = false) MultipartFile questionImage

    ) {

        Question question = new Question();
        question.setQuestionText(questionText);
        question.setQuestionType(questionType);
        question.setContentType(contentType);

        if (questionImage != null && !questionImage.isEmpty()) {

            String imageUrl = FileUploadUtil
                    .saveFile(questionImage, "questions");

            question.setQuestionImageUrl(imageUrl);
        }

        return ResponseEntity.ok(
                questionRepository.save(question)
        );
    }

    // ================= GET ALL QUESTIONS =================
    @GetMapping
    public ResponseEntity<List<Question>> getAllQuestions() {
        return ResponseEntity.ok(
                questionRepository.findAll()
        );
    }

    // ================= GET QUESTION BY ID =================
    @GetMapping("/{questionId}")
    public ResponseEntity<Question> getQuestion(
            @PathVariable Long questionId) {

        return ResponseEntity.ok(
                questionRepository.findById(questionId)
                        .orElseThrow(() ->
                                new IllegalStateException("Question not found"))
        );
    }

    // ================= DELETE QUESTION =================
    @DeleteMapping("/{questionId}")
    public ResponseEntity<Void> deleteQuestion(
            @PathVariable Long questionId) {

        questionRepository.deleteById(questionId);
        return ResponseEntity.noContent().build();
    }
}
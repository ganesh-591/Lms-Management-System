package com.lms.management.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.lms.management.model.QuestionOption;
import com.lms.management.service.QuestionOptionService;
import com.lms.management.util.FileUploadUtil;

@RestController
@RequestMapping("/api/questions/{questionId}/options")
public class QuestionOptionController {

    private final QuestionOptionService questionOptionService;

    public QuestionOptionController(
            QuestionOptionService questionOptionService) {
        this.questionOptionService = questionOptionService;
    }

    // 🔥 POST 4 OPTIONS AT ONCE (TEXT + IMAGE SUPPORT)
    @PostMapping(consumes = "multipart/form-data")
    @PreAuthorize("hasAuthority('QUESTION_OPTION_MANAGE')")
    public ResponseEntity<List<QuestionOption>> addOptions(
            @PathVariable Long questionId,

            @RequestParam(required = false) List<String> optionText,
            @RequestParam(required = false) List<Boolean> isCorrect,
            @RequestParam(required = false) List<MultipartFile> optionImage
    ) {

        List<QuestionOption> options = new ArrayList<>();

        int total = 0;

        if (optionText != null) total = optionText.size();
        if (optionImage != null) total = optionImage.size();

        for (int i = 0; i < total; i++) {

            QuestionOption option = new QuestionOption();
            option.setQuestionId(questionId);

            // Text
            if (optionText != null && i < optionText.size()) {
                option.setOptionText(optionText.get(i));
            }

            // Correct flag
            if (isCorrect != null && i < isCorrect.size()) {
                option.setIsCorrect(isCorrect.get(i));
            } else {
                option.setIsCorrect(false);
            }

            // Image
            if (optionImage != null && i < optionImage.size()
                    && !optionImage.get(i).isEmpty()) {

                String imageUrl = FileUploadUtil.saveFile(
                        optionImage.get(i),
                        "options"
                );

                option.setOptionImageUrl(imageUrl);
            }

            options.add(option);
        }

        return ResponseEntity.ok(
                questionOptionService.addOptions(questionId, options)
        );
    }

    // GET OPTIONS
    @GetMapping
    @PreAuthorize("hasAuthority('QUESTION_OPTION_VIEW')")
    public ResponseEntity<List<QuestionOption>> getOptions(
            @PathVariable Long questionId) {

        return ResponseEntity.ok(
                questionOptionService.getOptionsByQuestion(questionId));
    }

    // DELETE OPTION
    @DeleteMapping("/{optionId}")
    @PreAuthorize("hasAuthority('QUESTION_OPTION_MANAGE')")
    public ResponseEntity<Void> deleteOption(
            @PathVariable Long optionId) {

        questionOptionService.deleteOption(optionId);
        return ResponseEntity.noContent().build();
    }

 // ================= UPDATE OPTION =================
    @PutMapping(value = "/{optionId}", consumes = "application/json")
    @PreAuthorize("hasAuthority('QUESTION_OPTION_MANAGE')")
    public ResponseEntity<QuestionOption> updateOption(
            @PathVariable Long questionId,
            @PathVariable Long optionId,
            @RequestBody QuestionOption request) {

        return ResponseEntity.ok(
                questionOptionService.updateOption(questionId, optionId, request)
        );
    }
}
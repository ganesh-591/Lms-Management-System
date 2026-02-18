package com.lms.management.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.management.exception.ResourceNotFoundException;
import com.lms.management.model.Exam;
import com.lms.management.model.ExamSettings;
import com.lms.management.repository.ExamRepository;
import com.lms.management.repository.ExamSettingsRepository;
import com.lms.management.service.ExamSettingsService;

@Service
@Transactional
public class ExamSettingsServiceImpl implements ExamSettingsService {

    private final ExamSettingsRepository examSettingsRepository;
    private final ExamRepository examRepository;

    public ExamSettingsServiceImpl(
            ExamSettingsRepository examSettingsRepository,
            ExamRepository examRepository) {
        this.examSettingsRepository = examSettingsRepository;
        this.examRepository = examRepository;
    }

    @Override
    public ExamSettings saveSettings(Long examId, ExamSettings settings) {

        Exam exam = examRepository.findById(examId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Exam not found with id: " + examId));

        // 🔒 ENTERPRISE RULE: settings are mutable ONLY in DRAFT
        if (!"DRAFT".equals(exam.getStatus())) {
            throw new IllegalStateException(
                    "Exam settings can be modified only when exam is in DRAFT state");
        }

        settings.setExamId(examId);

        // UPSERT (safe & idempotent)
        return examSettingsRepository.findByExamId(examId)
                .map(existing -> {
                    settings.setSettingsId(existing.getSettingsId());
                    return examSettingsRepository.save(settings);
                })
                .orElseGet(() -> examSettingsRepository.save(settings));
    }

    @Override
    public ExamSettings getSettingsByExamId(Long examId) {
        return examSettingsRepository.findByExamId(examId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Exam settings not found for examId: " + examId));
    }

    // 🔥 Toggle MCQ option shuffle
    @Override
    public ExamSettings updateShuffleOptions(Long examId, Boolean shuffle) {

        Exam exam = examRepository.findById(examId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Exam not found with id: " + examId));

        // 🔒 Keep same enterprise rule
        if (!"DRAFT".equals(exam.getStatus())) {
            throw new IllegalStateException(
                    "Exam settings can be modified only when exam is in DRAFT state");
        }

        ExamSettings settings = examSettingsRepository.findByExamId(examId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Exam settings not found for examId: " + examId));

        settings.setShuffleOptions(shuffle != null ? shuffle : false);

        return examSettingsRepository.save(settings);
    }
}

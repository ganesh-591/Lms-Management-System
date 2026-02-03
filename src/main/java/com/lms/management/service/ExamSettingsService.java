package com.lms.management.service;

import com.lms.management.model.ExamSettings;

public interface ExamSettingsService {

    ExamSettings saveSettings(Long examId, ExamSettings settings);

    ExamSettings getSettingsByExamId(Long examId);
}

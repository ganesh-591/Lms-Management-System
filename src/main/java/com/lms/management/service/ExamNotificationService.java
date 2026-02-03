package com.lms.management.service;

import com.lms.management.model.ExamNotification;

public interface ExamNotificationService {

    ExamNotification saveNotification(Long examId, ExamNotification notification);

    ExamNotification getNotificationByExamId(Long examId);
}

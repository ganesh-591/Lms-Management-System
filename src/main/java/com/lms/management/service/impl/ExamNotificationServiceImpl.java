package com.lms.management.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lms.management.model.Exam;
import com.lms.management.model.ExamNotification;
import com.lms.management.repository.ExamNotificationRepository;
import com.lms.management.repository.ExamRepository;
import com.lms.management.service.ExamNotificationService;

@Service
@Transactional
public class ExamNotificationServiceImpl implements ExamNotificationService {

    private final ExamNotificationRepository notificationRepository;
    private final ExamRepository examRepository;

    public ExamNotificationServiceImpl(
            ExamNotificationRepository notificationRepository,
            ExamRepository examRepository) {
        this.notificationRepository = notificationRepository;
        this.examRepository = examRepository;
    }

    @Override
    public ExamNotification saveNotification(
            Long examId, ExamNotification notification) {

        Exam exam = examRepository.findById(examId)
                .orElseThrow(() ->
                        new IllegalStateException("Exam not found"));

        // 🔒 Only editable in DRAFT
        if (!"DRAFT".equals(exam.getStatus())) {
            throw new IllegalStateException(
                    "Exam notification can be set only in DRAFT");
        }

        notification.setExamId(examId);

        return notificationRepository.findByExamId(examId)
                .map(existing -> {
                    notification.setNotificationId(
                            existing.getNotificationId());
                    return notificationRepository.save(notification);
                })
                .orElseGet(() ->
                        notificationRepository.save(notification));
    }

    @Override
    public ExamNotification getNotificationByExamId(Long examId) {
        return notificationRepository.findByExamId(examId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Exam notification not found"));
    }
}

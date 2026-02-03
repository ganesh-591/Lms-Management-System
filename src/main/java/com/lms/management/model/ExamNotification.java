package com.lms.management.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "exam_notification",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "exam_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
public class ExamNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;

    @Column(name = "exam_id", nullable = false)
    private Long examId;

    @Column(name = "scheduled_notification", nullable = false)
    private Boolean scheduledNotification;

    // NONE / 1H / 24H
    @Column(name = "reminder_before", nullable = false)
    private String reminderBefore;

    @Column(name = "feedback_after_exam", nullable = false)
    private Boolean feedbackAfterExam;
}

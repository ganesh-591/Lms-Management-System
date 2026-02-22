package com.lms.management.model;

import java.time.LocalDateTime;

import com.lms.management.enums.CertificateStatus;
import com.lms.management.enums.TargetType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "certificate",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_certificate_id", columnNames = "certificate_id"),
                @UniqueConstraint(name = "uq_verification_token", columnNames = "verification_token"),
                @UniqueConstraint(name = "uq_user_target", columnNames = {"user_id", "target_type", "target_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Certificate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "certificate_id", nullable = false)
    private String certificateId;

    @Column(name = "verification_token", nullable = false)
    private String verificationToken;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "student_name", nullable = false)
    private String studentName;   // 🔥 Snapshot field

    @Column(name = "target_type", nullable = false)
    private TargetType targetType;

    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @Column(name = "event_title", nullable = false)
    private String eventTitle;    // 🔥 Snapshot field

    @Column(name = "template_id")
    private Long templateId;

    private Double score;

    @Column(name = "issued_date", nullable = false)
    private LocalDateTime issuedDate;
    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private CertificateStatus status;

    @Column(name = "revoked_reason")
    private String revokedReason;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    @Column(nullable = false)
    private Integer version;

    @Column(name = "pdf_url")
    private String pdfUrl;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
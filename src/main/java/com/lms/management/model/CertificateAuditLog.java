package com.lms.management.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "certificate_audit_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CertificateAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "certificate_id", nullable = false)
    private Long certificateId;

    @Column(nullable = false)
    private String action;  // GENERATED / EXPIRED / REVOKED

    @Column(name = "performed_by")
    private Long performedBy;  // null for system actions

    @Column(name = "action_date", nullable = false)
    private LocalDateTime actionDate;

    private String remarks;
}
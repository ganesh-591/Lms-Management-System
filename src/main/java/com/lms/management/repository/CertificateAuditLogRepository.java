package com.lms.management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lms.management.model.CertificateAuditLog;

public interface CertificateAuditLogRepository
        extends JpaRepository<CertificateAuditLog, Long> {

    List<CertificateAuditLog> findByCertificateId(Long certificateId);
}
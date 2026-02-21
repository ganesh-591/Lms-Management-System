package com.lms.management.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lms.management.model.CertificateRule;

public interface CertificateRuleRepository extends JpaRepository<CertificateRule, Long> {

    Optional<CertificateRule> findByTargetTypeAndTargetIdAndIsActiveTrue(
            String targetType,
            Long targetId
    );
}
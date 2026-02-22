package com.lms.management.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lms.management.enums.TargetType;
import com.lms.management.model.CertificateProgress;

public interface CertificateProgressRepository
        extends JpaRepository<CertificateProgress, Long> {

    Optional<CertificateProgress> findByUserIdAndTargetTypeAndTargetId(
            Long userId,
            TargetType targetType,
            Long targetId
    );
}
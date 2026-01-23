package com.lms.management.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lms.management.model.StudentBatchTransfer;

public interface StudentBatchTransferRepository
        extends JpaRepository<StudentBatchTransfer, Long> {

    List<StudentBatchTransfer> findByStudentId(Long studentId);

    List<StudentBatchTransfer> findByCourseId(Long courseId);
}

package com.lms.management.service;

import java.util.List;

import com.lms.management.model.StudentBatch;

public interface StudentBatchService {

    StudentBatch enrollStudent(StudentBatch studentBatch);

    StudentBatch updateEnrollment(Long studentBatchId, StudentBatch updated);

    List<StudentBatch> getStudentsByBatch(Long batchId);

    StudentBatch getStudentCurrentBatch(Long studentId);

    void removeStudent(Long studentBatchId);
}

package com.lms.management.service;

import com.lms.management.model.Certificate;

public interface CertificatePdfService {
    String generatePdf(Certificate certificate, String studentName, String examTitle);
}

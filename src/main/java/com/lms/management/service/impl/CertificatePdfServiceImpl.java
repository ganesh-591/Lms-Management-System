package com.lms.management.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

import org.springframework.stereotype.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.QRCodeWriter;
import com.lms.management.model.Certificate;
import com.lms.management.service.CertificatePdfService;
import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

@Service
public class CertificatePdfServiceImpl implements CertificatePdfService {

    @Override
    public String generatePdf(Certificate certificate,
                              String studentName,
                              String eventTitle) {

        try {

            String template = new String(
                    Files.readAllBytes(
                            Paths.get("src/main/resources/templates/certificate-template.html"))
            );

            // 🔥 UPDATED: QR now points to public verification page
            String verifyUrl = "http://localhost:5151/api/certificates/public/"
                    + certificate.getVerificationToken();

            String qrBase64 = generateQrCodeBase64(verifyUrl);

            template = template.replace("{{studentName}}", studentName);
            template = template.replace("{{examTitle}}", eventTitle);
            template = template.replace("{{score}}",
                    String.valueOf(certificate.getScore()));
            template = template.replace("{{date}}",
                    certificate.getIssuedDate()
                            .format(DateTimeFormatter.ofPattern("dd MMM yyyy")));
            template = template.replace("{{certificateId}}",
                    certificate.getCertificateId());
            template = template.replace("{{qrCode}}", qrBase64);

            File folder = new File("certificates");
            if (!folder.exists()) folder.mkdirs();

            String outputPath =
                    "certificates/" + certificate.getCertificateId() + ".pdf";

            try (OutputStream os = new FileOutputStream(outputPath)) {
                PdfRendererBuilder builder = new PdfRendererBuilder();
                builder.withHtmlContent(template, null);
                builder.toStream(os);
                builder.run();
            }

            return outputPath;

        } catch (Exception e) {
            throw new RuntimeException("PDF generation failed", e);
        }
    }

    private String generateQrCodeBase64(String text) throws Exception {
        QRCodeWriter writer = new QRCodeWriter();
        var bitMatrix =
                writer.encode(text, BarcodeFormat.QR_CODE, 200, 200);

        ByteArrayOutputStream pngOutputStream =
                new ByteArrayOutputStream();

        com.google.zxing.client.j2se.MatrixToImageWriter
                .writeToStream(bitMatrix, "PNG", pngOutputStream);

        return Base64.getEncoder()
                .encodeToString(pngOutputStream.toByteArray());
    }
}
package com.lms.management.converter;

import com.lms.management.enums.CertificateStatus;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class CertificateStatusConverter implements AttributeConverter<CertificateStatus, Integer> {

    @Override
    public Integer convertToDatabaseColumn(CertificateStatus attribute) {
        return attribute != null ? attribute.getValue() : null;
    }

    @Override
    public CertificateStatus convertToEntityAttribute(Integer dbData) {
        return dbData != null ? CertificateStatus.fromValue(dbData) : null;
    }
}

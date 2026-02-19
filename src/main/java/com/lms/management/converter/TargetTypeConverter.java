package com.lms.management.converter;

import com.lms.management.enums.TargetType;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TargetTypeConverter implements AttributeConverter<TargetType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(TargetType attribute) {
        return attribute != null ? attribute.getValue() : null;
    }

    @Override
    public TargetType convertToEntityAttribute(Integer dbData) {
        return dbData != null ? TargetType.fromValue(dbData) : null;
    }
}

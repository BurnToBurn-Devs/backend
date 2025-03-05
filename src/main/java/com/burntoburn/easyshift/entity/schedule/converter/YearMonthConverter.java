package com.burntoburn.easyshift.entity.schedule.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.YearMonth;

@Converter(autoApply = true)
public class YearMonthConverter implements AttributeConverter<YearMonth, String> {

    @Override
    public String convertToDatabaseColumn(YearMonth attribute) {
        return (attribute != null) ? attribute.toString() : null; // "YYYY-MM" 형식으로 변환
    }

    @Override
    public YearMonth convertToEntityAttribute(String dbData) {
        return (dbData != null && !dbData.isEmpty()) ? YearMonth.parse(dbData) : null;
    }
}
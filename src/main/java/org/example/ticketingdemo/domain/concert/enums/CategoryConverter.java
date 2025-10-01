package org.example.ticketingdemo.domain.concert.enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class CategoryConverter implements AttributeConverter<Category, String> {
    @Override
    public String convertToDatabaseColumn(Category attribute) {
        return attribute != null ? attribute.getDbValue() : null;
    }

    @Override
    public Category convertToEntityAttribute(String dbData) {
        return dbData != null ? Category.fromDbValue(dbData) : null;
    }
}

package com.finstuff.security.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Converter(autoApply = true)  // Автоматическое применение для всех List<String>
public class StringListConverter implements AttributeConverter<List<String>, String[]> {

    @Override
    public String[] convertToDatabaseColumn(List<String> list) {
        // List → String[] (для сохранения в БД)
        return list == null ? null : list.toArray(new String[0]);
    }

    @Override
    public List<String> convertToEntityAttribute(String[] array) {
        // String[] → List (для загрузки из БД)
        return array == null ? Collections.emptyList() : Arrays.asList(array);
    }
}

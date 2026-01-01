package com.tachnique.app.quiz_service_impl.mapper;

import com.tachnique.app.dto.QuestionOptionDto;
import com.tachnique.app.quiz_service_impl.entity.QuestionOptionEntity;

public class QuestionOptionMapper {
    public static QuestionOptionEntity toEntity(QuestionOptionDto dto) {
        if (dto == null) return null;
        // Note: question relationship is handled at the QuestionMapper level
        return QuestionOptionEntity.builder()
                .id(dto.getId())
                .text(dto.getText())
                .isCorrect(dto.isCorrect())
                .build();
    }

    public static QuestionOptionDto toDto(QuestionOptionEntity entity) {
        if (entity == null) return null;
        return QuestionOptionDto.builder()
                .id(entity.getId())
                .text(entity.getText())
                .isCorrect(entity.isCorrect())
                .build();
    }
}

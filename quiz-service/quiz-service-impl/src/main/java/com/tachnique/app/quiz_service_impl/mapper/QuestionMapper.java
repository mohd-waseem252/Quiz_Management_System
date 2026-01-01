package com.tachnique.app.quiz_service_impl.mapper;

import com.tachnique.app.dto.QuestionDto;
import com.tachnique.app.quiz_service_impl.entity.QuestionEntity;
import com.tachnique.app.quiz_service_impl.entity.QuestionOptionEntity;
import com.tachnique.app.quiz_service_impl.enums.QuestionType;

import java.util.stream.Collectors;

public class QuestionMapper {
    public static QuestionEntity toEntity(QuestionDto dto) {
        if (dto == null) return null;
        QuestionEntity entity = QuestionEntity.builder()
                .id(dto.getId())
                .text(dto.getText())
                .type(dto.getType() != null ? QuestionType.valueOf(dto.getType()) : null)
                .options(dto.getOptions() != null ? dto.getOptions().stream().map(QuestionOptionMapper::toEntity).collect(Collectors.toList()) : null)
                .build();
        if (entity.getOptions() != null) {
            for (QuestionOptionEntity opt : entity.getOptions()) {
                opt.setQuestion(entity);
            }
        }
        return entity;
    }

    public static QuestionDto toDto(QuestionEntity entity) {
        if (entity == null) return null;
        return QuestionDto.builder()
                .id(entity.getId())
                .text(entity.getText())
                .type(entity.getType() != null ? entity.getType().name() : null)
                .options(entity.getOptions() != null ? entity.getOptions().stream().map(QuestionOptionMapper::toDto).collect(Collectors.toList()) : null)
                .build();
    }
}

package com.tachnique.app.quiz_service_impl.mapper;

import com.tachnique.app.dto.QuizDto;
import com.tachnique.app.quiz_service_impl.entity.QuizEntity;
import com.tachnique.app.quiz_service_impl.entity.QuestionEntity;
import java.util.stream.Collectors;

public class QuizMapper {
    public static QuizEntity toEntity(QuizDto dto) {
        if (dto == null) return null;
        QuizEntity entity = QuizEntity.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .questions(dto.getQuestions() != null ? dto.getQuestions().stream().map(QuestionMapper::toEntity).collect(Collectors.toList()) : null)
                .build();
        if (entity.getQuestions() != null) {
            for (QuestionEntity q : entity.getQuestions()) {
                q.setQuiz(entity);
            }
        }
        return entity;
    }

    public static QuizDto toDto(QuizEntity entity) {
        if (entity == null) return null;
        return QuizDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .questions(entity.getQuestions() != null ? entity.getQuestions().stream().map(QuestionMapper::toDto).collect(Collectors.toList()) : null)
                .build();
    }
}

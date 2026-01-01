package com.tachnique.app.quiz_service_impl.serviceImpl;

import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.tachnique.app.dto.QuizDto;
import com.tachnique.app.quiz_service_impl.mapper.QuizMapper;
import com.tachnique.app.quiz_service_impl.repository.QuizRepository;
import com.tachnique.app.service.QuizService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;

    @Override
    public QuizDto createQuiz(QuizDto quizDto) {
        return QuizMapper.toDto(quizRepository.save(QuizMapper.toEntity(quizDto)));
    }

    @Override
    public List<QuizDto> getQuizzes() {
        return quizRepository.findAll().stream()
                .map(QuizMapper::toDto)
                .toList();
    }
}

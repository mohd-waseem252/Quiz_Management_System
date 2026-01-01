package com.tachnique.app.quiz_service_impl.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tachnique.app.quiz_service_impl.entity.QuizEntity;

public interface QuizRepository extends JpaRepository<QuizEntity, Long> {

}

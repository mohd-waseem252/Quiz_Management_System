package com.tachnique.app.quiz_ui.modules.publicview;

import com.tachnique.app.dto.QuizDto;

import java.util.List;

public interface PublicView {
    void showQuizzes(List<QuizDto> quizzes);
    void showError(String message);
    void setPresenter(PublicPresenter presenter);
}


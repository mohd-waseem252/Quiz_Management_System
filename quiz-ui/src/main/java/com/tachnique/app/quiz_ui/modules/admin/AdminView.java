package com.tachnique.app.quiz_ui.modules.admin;

import java.util.List;

import com.tachnique.app.dto.QuizDto;

public interface AdminView {

    void showQuizzes(List<QuizDto> quizzes);
    void showError(String message);
    void showSuccess(String message);
    void setPresenter(AdminPresenter presenter);
}

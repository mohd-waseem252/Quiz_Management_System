package com.tachnique.app.quiz_ui.modules.admin;

import org.springframework.stereotype.Component;

import com.tachnique.app.dto.QuizDto;
import com.tachnique.app.service.QuizService;
import com.vaadin.flow.spring.annotation.UIScope;

@UIScope
@Component
public class AdminPresenter {

    private AdminView view;
    private QuizService quizService;

    public AdminPresenter(QuizService quizService) {
        this.quizService = quizService;
    }


    public void bind(AdminViewImpl adminView) {
        this.view = adminView;
        this.view.setPresenter(this);
    }

    public void saveQuiz(QuizDto quizDto) {
        try {
            QuizDto saved = quizService.createQuiz(quizDto);
            view.showSuccess("Quiz saved: " + saved.getTitle());
            // could reload list here when implemented
        } catch (Exception ex) {
            view.showError("Failed to save quiz: " + ex.getMessage());
        }
    }
}

package com.tachnique.app.quiz_ui.modules.publicview;

import com.tachnique.app.dto.QuizDto;
import com.tachnique.app.service.QuizService;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.stereotype.Component;

import java.util.List;

@UIScope
@Component
public class PublicPresenter {
    private PublicView view;
    private final QuizService quizService;

    public PublicPresenter(QuizService quizService) {
        this.quizService = quizService;
    }

    public void bind(PublicView view) {
        this.view = view;
        this.view.setPresenter(this);
    }

    public void loadQuizzes() {
        try {
            List<QuizDto> quizzes = quizService.getQuizzes();
            view.showQuizzes(quizzes);
        } catch (Exception ex) {
            view.showError("Failed to load quizzes: " + ex.getMessage());
        }
    }
}


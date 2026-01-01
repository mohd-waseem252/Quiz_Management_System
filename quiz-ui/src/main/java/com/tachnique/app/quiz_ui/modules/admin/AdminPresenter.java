package com.tachnique.app.quiz_ui.modules.admin;

import org.springframework.stereotype.Component;

import com.tachnique.app.dto.QuizDto;
import com.tachnique.app.service.QuizService;
import com.vaadin.flow.spring.annotation.UIScope;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

@UIScope
@Component
public class AdminPresenter {

    private AdminView view;
    private final QuizService quizService;
    private final List<QuizDto> cachedQuizzes = new CopyOnWriteArrayList<>();

    public AdminPresenter(QuizService quizService) {
        this.quizService = quizService;
    }


    public void bind(AdminViewImpl adminView) {
        this.view = adminView;
        this.view.setPresenter(this);
        loadQuizzes();
    }

    public void saveQuiz(QuizDto quizDto) {
        try {
            QuizDto saved = quizService.createQuiz(quizDto);
            view.showSuccess("Quiz saved: " + saved.getTitle());
            loadQuizzes();
        } catch (Exception ex) {
            view.showError("Failed to save quiz: " + ex.getMessage());
        }
    }

    public void loadQuizzes() {
        try {
            List<QuizDto> quizzes = quizService.getQuizzes();
            cachedQuizzes.clear();
            cachedQuizzes.addAll(quizzes);
            view.showQuizzes(quizzes);
        } catch (Exception ex) {
            view.showError("Failed to load quizzes: " + ex.getMessage());
        }
    }

    public boolean isTitleUnique(String title) {
        if (title == null) return false;
        String normalized = title.trim().toLowerCase();
        return cachedQuizzes.stream()
                .map(QuizDto::getTitle)
                .filter(Objects::nonNull)
                .map(t -> t.trim().toLowerCase())
                .noneMatch(t -> t.equals(normalized));
    }
}

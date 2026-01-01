package com.tachnique.app.quiz_ui.modules.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.tachnique.app.dto.QuizDto;
import com.tachnique.app.dto.QuestionDto;
import com.tachnique.app.dto.QuestionOptionDto;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.notification.Notification;

@Route(value = "admin")
@PageTitle("Admin Panel")
public class AdminViewImpl extends VerticalLayout implements AdminView {

    private AdminPresenter presenter;
    private final Grid<QuizDto> quizGrid = new Grid<>();

    public AdminViewImpl(AdminPresenter presenter) {
        presenter.bind(this);
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        quizGrid.addColumn(QuizDto::getId).setHeader("ID").setAutoWidth(true);
        quizGrid.addColumn(QuizDto::getTitle).setHeader("Title").setAutoWidth(true);

        Button addQuizButton = new Button("Add Quiz");
        addQuizButton.addClickListener(e -> {
            openDialog();
        });
        add(quizGrid, addQuizButton);

    }

    private void openDialog() {
        Dialog dialog = new Dialog();
        dialog.setWidth(50, Unit.PERCENTAGE);
        dialog.setHeightFull();
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(false);

        TextField title = new TextField("Quiz Title");
        Button addQuestionButton = new Button("Add Question");
        HorizontalLayout titleLayout = new HorizontalLayout(title, addQuestionButton);
        titleLayout.setWidthFull();
        title.setWidth("100%");
        titleLayout.setFlexGrow(1, title);

        VerticalLayout questionsContainer = new VerticalLayout();
        questionsContainer.setPadding(false);
        questionsContainer.setSpacing(false);
        questionsContainer.setWidthFull();

        // add first question by default
        QuestionEditor first = new QuestionEditor();
        first.setSummaryText("Question 1");
        questionsContainer.add(first);

        addQuestionButton.addClickListener(e -> {
            int count = questionsContainer.getComponentCount() + 1;
            QuestionEditor editor = new QuestionEditor();
            editor.setSummaryText("Question " + count);
            questionsContainer.add(editor);
        });

        Button save = new Button("Save");
        Button cancel = new Button("Cancel");
        HorizontalLayout actions = new HorizontalLayout(save, cancel);

        save.addClickListener(e -> {
            if (title.isEmpty()) {
                Notification.show("Quiz title is required");
                return;
            }
            boolean allValid = questionsContainer.getChildren()
                    .filter(c -> c instanceof QuestionEditor)
                    .map(c -> (QuestionEditor) c)
                    .map(QuestionEditor::validate)
                    .reduce(true, (a, b) -> a && b);
            if (!allValid) return;

            // collect questions
            var editors = questionsContainer.getChildren()
                    .filter(c -> c instanceof QuestionEditor)
                    .map(c -> (QuestionEditor) c)
                    .collect(Collectors.toList());

            var questionDtos = new ArrayList<QuestionDto>();
            for (QuestionEditor qe : editors) {
                String type = qe.getQuestionType();
                QuestionDto qd = QuestionDto.builder()
                        .text(qe.getQuestionText())
                        .type(type)
                        .build();
                switch (type) {
                    case "MCQ": {
                        var options = qe.getMcqOptions();
                        Integer correct = qe.getMcqCorrectIndex();
                        var optionDtos = new ArrayList<QuestionOptionDto>();
                        for (int i = 0; i < options.size(); i++) {
                            optionDtos.add(QuestionOptionDto.builder()
                                    .text(options.get(i))
                                    .isCorrect(correct != null && correct == (i + 1))
                                    .build());
                        }
                        qd.setOptions(optionDtos);
                        break;
                    }
                    case "TRUE_FALSE": {
                        Boolean ans = qe.getTrueFalseAnswer();
                        var optionDtos = new ArrayList<QuestionOptionDto>();
                        optionDtos.add(QuestionOptionDto.builder().text("True").isCorrect(Boolean.TRUE.equals(ans)).build());
                        optionDtos.add(QuestionOptionDto.builder().text("False").isCorrect(Boolean.FALSE.equals(ans)).build());
                        qd.setOptions(optionDtos);
                        break;
                    }
                    case "TEXT": {
                        String ans = qe.getTextAnswer();
                        var optionDtos = new ArrayList<QuestionOptionDto>();
                        optionDtos.add(QuestionOptionDto.builder().text(ans).isCorrect(true).build());
                        qd.setOptions(optionDtos);
                        break;
                    }
                    default: break;
                }
                questionDtos.add(qd);
            }

            QuizDto quizDto = QuizDto.builder()
                    .title(title.getValue())
                    .questions(questionDtos)
                    .build();

            presenter.saveQuiz(quizDto);
            dialog.close();
        });

        cancel.addClickListener(e -> dialog.close());

        VerticalLayout content = new VerticalLayout(titleLayout, questionsContainer, actions);
        content.setWidthFull();
        dialog.add(content);
        dialog.open();
    }

    @Override
    public void showQuizzes(List<QuizDto> quizzes) {
        quizGrid.setItems(quizzes);
    }

    @Override
    public void showError(String message) {
        Notification.show(message);
    }

    @Override
    public void showSuccess(String message) {
        Notification.show(message);
    }

    @Override
    public void setPresenter(AdminPresenter presenter) {
        this.presenter = presenter;
    }
}

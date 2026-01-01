package com.tachnique.app.quiz_ui.modules.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.tachnique.app.dto.QuizDto;
import com.tachnique.app.dto.QuestionDto;
import com.tachnique.app.dto.QuestionOptionDto;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.server.VaadinSession;
import com.tachnique.app.quiz_ui.layout.MainLayout;

@Route(value = "admin", layout = MainLayout.class)
@PageTitle("Admin Panel")
public class AdminViewImpl extends VerticalLayout implements AdminView, BeforeEnterObserver {

    private AdminPresenter presenter;
    private final Grid<QuizDto> quizGrid = new Grid<>();

    public AdminViewImpl(AdminPresenter presenter) {
        presenter.bind(this);
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        quizGrid.addColumn(QuizDto::getId).setHeader("ID").setAutoWidth(true);
        quizGrid.addColumn(QuizDto::getTitle).setHeader("Title").setAutoWidth(true);
        quizGrid.addComponentColumn(q -> {
            Button viewBtn = new Button("View");
            viewBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_TERTIARY_INLINE);
            viewBtn.addClickListener(e -> openViewDialog(q));
            return viewBtn;
        }).setHeader("Actions").setAutoWidth(true);

        Button addQuizButton = new Button("Add Quiz");
        addQuizButton.addClickListener(e -> {
            openDialog();
        });
        add(addQuizButton, quizGrid);

        presenter.loadQuizzes();

    }

    private void notifyCenter(String message) {
        Notification n = new Notification(message, 3000);
        n.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
        n.setPosition(Notification.Position.MIDDLE);
        n.open();
    }

    private void openDialog() {
        Dialog dialog = new Dialog();
        dialog.setWidth(50, Unit.PERCENTAGE);
        dialog.setHeightFull();
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(false);

        TextField title = new TextField("Quiz Title");
        title.setRequired(true);
        Button addQuestionButton = new Button("Add Question");
        HorizontalLayout titleLayout = new HorizontalLayout(title, addQuestionButton);
        titleLayout.setAlignItems(Alignment.END);
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
        // Actions moved to footer and centered
        HorizontalLayout footer = new HorizontalLayout(save, cancel);
        footer.setWidthFull();
        footer.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        dialog.getFooter().add(footer);

        save.addClickListener(e -> {
            if (title.isEmpty()) {
                notifyCenter("Quiz title is required");
                return;
            }
            if (!presenter.isTitleUnique(title.getValue())) {
                notifyCenter("Quiz title must be unique");
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
            notifyCenter("Quiz saved successfully");
            dialog.close();
        });

        cancel.addClickListener(e -> dialog.close());

        VerticalLayout content = new VerticalLayout(titleLayout, questionsContainer);
        content.setWidthFull();
        dialog.add(content);
        dialog.open();
    }

    private void openViewDialog(QuizDto quiz) {
        Dialog dialog = new Dialog();
        dialog.setWidth(50, Unit.PERCENTAGE);
        dialog.setHeightFull();
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(true);

        TextField title = new TextField("Quiz Title");
        title.setValue(quiz.getTitle() != null ? quiz.getTitle() : "");
        title.setReadOnly(true);
        // remove Add Question button in view-only mode
        HorizontalLayout titleLayout = new HorizontalLayout(title);
        titleLayout.setAlignItems(Alignment.END);
        titleLayout.setWidthFull();
        title.setWidth("100%");
        titleLayout.setFlexGrow(1, title);

        VerticalLayout questionsContainer = new VerticalLayout();
        questionsContainer.setPadding(false);
        questionsContainer.setSpacing(false);
        questionsContainer.setWidthFull();

        // populate existing questions
        List<QuestionDto> qs = quiz.getQuestions() != null ? quiz.getQuestions() : List.of();
        int idx = 1;
        for (QuestionDto qd : qs) {
            QuestionEditor editor = new QuestionEditor();
            editor.setSummaryText("Question " + idx++);
            // set values
            editor.questionField.setValue(qd.getText() != null ? qd.getText() : "");
            editor.questionField.setReadOnly(true);
            editor.typeCombo.setValue(qd.getType());
            editor.typeCombo.setReadOnly(true);
            editor.renderDynamicArea();
            switch (qd.getType()) {
                case "MCQ":
                    for (int i = 0; i < editor.mcqOptions.size() && i < qd.getOptions().size(); i++) {
                        editor.mcqOptions.get(i).setValue(qd.getOptions().get(i).getText());
                        editor.mcqOptions.get(i).setReadOnly(true);
                        if (qd.getOptions().get(i).isCorrect()) editor.mcqCorrect.setValue(i + 1);
                    }
                    editor.mcqCorrect.setReadOnly(true);
                    break;
                case "TRUE_FALSE":
                    Boolean ans = qd.getOptions().stream().anyMatch(o -> o.getText().equals("True") && o.isCorrect());
                    editor.tfGroup.setValue(ans);
                    editor.tfGroup.setReadOnly(true);
                    break;
                case "TEXT":
                    if (!qd.getOptions().isEmpty()) editor.textAnswer.setValue(qd.getOptions().get(0).getText());
                    editor.textAnswer.setReadOnly(true);
                    break;
                default:
                    break;
            }
            questionsContainer.add(editor);
        }

        Button close = new Button("Close");
        HorizontalLayout footer = new HorizontalLayout(close);
        footer.setWidthFull();
        footer.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        dialog.getFooter().add(footer);
        close.addClickListener(e -> dialog.close());

        VerticalLayout content = new VerticalLayout(titleLayout, questionsContainer);
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
        notifyCenter(message);
    }

    @Override
    public void showSuccess(String message) {
        notifyCenter(message);
    }

    @Override
    public void setPresenter(AdminPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        var user = (com.tachnique.app.dto.UserDto) VaadinSession.getCurrent().getAttribute("user");
        if (user == null || user.getRole() == null || !"ADMIN".equalsIgnoreCase(user.getRole())) {
            event.forwardTo("login");
        }
    }
}

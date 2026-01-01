package com.tachnique.app.quiz_ui.modules.publicview;

import com.tachnique.app.dto.QuestionDto;
import com.tachnique.app.dto.QuestionOptionDto;
import com.tachnique.app.dto.QuizDto;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Pre;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Route(value = "quiz")
@PageTitle("Attempt Quiz")
public class PublicViewImpl extends VerticalLayout implements PublicView {

    private PublicPresenter presenter;
    private final Grid<QuizDto> quizGrid = new Grid<>();

    @Autowired
    public PublicViewImpl(PublicPresenter presenter) {
        this.presenter = presenter;
        presenter.bind(this);

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        quizGrid.addColumn(QuizDto::getId).setHeader("ID").setAutoWidth(true);
        quizGrid.addColumn(QuizDto::getTitle).setHeader("Title").setAutoWidth(true);
        quizGrid.addComponentColumn(q -> {
            Button startBtn = new Button("Start");
            startBtn.addClickListener(e -> openAttemptDialog(q));
            return startBtn;
        }).setHeader("Actions").setAutoWidth(true);

        add(quizGrid);
        presenter.loadQuizzes();
    }

    private void notifyCenter(String message) {
        Notification n = new Notification(message, 3000);
        n.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
        n.setPosition(Notification.Position.MIDDLE);
        n.open();
    }

    private void openAttemptDialog(QuizDto quiz) {
        Dialog dialog = new Dialog();
        dialog.setWidth(60, Unit.PERCENTAGE);
        dialog.setHeightFull();
        dialog.setCloseOnEsc(true);
        dialog.setCloseOnOutsideClick(false);

        TextField title = new TextField("Quiz Title");
        title.setValue(quiz.getTitle() != null ? quiz.getTitle() : "");
        title.setReadOnly(true);

        VerticalLayout questionsContainer = new VerticalLayout();
        questionsContainer.setPadding(false);
        questionsContainer.setSpacing(false);
        questionsContainer.setWidthFull();

        // answer capture structures
        Map<Long, Object> answers = new HashMap<>(); // questionId -> answer
        List<QuestionDto> questions = quiz.getQuestions() != null ? quiz.getQuestions() : List.of();
        int idx = 1;
        for (QuestionDto qd : questions) {
            VerticalLayout section = new VerticalLayout();
            section.setPadding(false);
            section.setSpacing(false);
            section.setWidthFull();

            TextArea qText = new TextArea("Question " + idx++);
            qText.setValue(qd.getText());
            qText.setReadOnly(true);
            qText.setWidthFull();

            switch (qd.getType()) {
                case "MCQ": {
                    RadioButtonGroup<Integer> group = new RadioButtonGroup<>();
                    group.setLabel("Select one answer");
                    List<QuestionOptionDto> opts = qd.getOptions();
                    List<Integer> items = new ArrayList<>();
                    for (int i = 0; i < opts.size(); i++) {
                        items.add(i + 1);
                    }
                    group.setItems(items);
                    group.setItemLabelGenerator(i -> opts.get(i - 1).getText());
                    group.addValueChangeListener(ev -> answers.put(qd.getId(), ev.getValue()));
                    section.add(qText, group);
                    break;
                }
                case "TRUE_FALSE": {
                    RadioButtonGroup<Boolean> group = new RadioButtonGroup<>();
                    group.setLabel("Select True or False");
                    group.setItems(Boolean.TRUE, Boolean.FALSE);
                    group.setItemLabelGenerator(b -> b ? "True" : "False");
                    group.addValueChangeListener(ev -> answers.put(qd.getId(), ev.getValue()));
                    section.add(qText, group);
                    break;
                }
                case "TEXT": {
                    TextField answer = new TextField("Answer");
                    answer.setWidthFull();
                    answer.addValueChangeListener(ev -> answers.put(qd.getId(), ev.getValue()));
                    section.add(qText, answer);
                    break;
                }
                default:
                    break;
            }
            questionsContainer.add(section);
        }

        Button submit = new Button("Submit");
        Button cancel = new Button("Cancel", ev -> dialog.close());
        HorizontalLayout footer = new HorizontalLayout(submit, cancel);
        footer.setWidthFull();
        footer.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        dialog.getFooter().add(footer);

        submit.addClickListener(e -> {
            // validate all answered
            for (QuestionDto qd : questions) {
                Object v = answers.get(qd.getId());
                if (v == null || (v instanceof String && ((String) v).trim().isEmpty())) {
                    notifyCenter("Please answer all questions before submitting");
                    return;
                }
            }
            // compute score
            int total = questions.size();
            int correct = 0;
            StringBuilder feedback = new StringBuilder();
            int qNum = 1;
            for (QuestionDto qd : questions) {
                boolean isCorrect;
                switch (qd.getType()) {
                    case "MCQ": {
                        Integer sel = (Integer) answers.get(qd.getId());
                        isCorrect = sel != null && qd.getOptions().get(sel - 1).isCorrect();
                        feedback.append("Q").append(qNum).append(": Correct answer is ");
                        for (int i = 0; i < qd.getOptions().size(); i++) {
                            if (qd.getOptions().get(i).isCorrect()) {
                                feedback.append(qd.getOptions().get(i).getText());
                                break;
                            }
                        }
                        feedback.append("\n");
                        break;
                    }
                    case "TRUE_FALSE": {
                        Boolean sel = (Boolean) answers.get(qd.getId());
                        boolean trueCorrect = qd.getOptions().stream().anyMatch(o -> o.getText().equals("True") && o.isCorrect());
                        isCorrect = sel != null && ((sel && trueCorrect) || (!sel && !trueCorrect));
                        feedback.append("Q").append(qNum).append(": Correct answer is ").append(trueCorrect ? "True" : "False").append("\n");
                        break;
                    }
                    case "TEXT": {
                        String sel = (String) answers.get(qd.getId());
                        String correctAns = qd.getOptions().isEmpty() ? "" : qd.getOptions().get(0).getText();
                        isCorrect = sel != null && sel.trim().equalsIgnoreCase(correctAns.trim());
                        feedback.append("Q").append(qNum).append(": Correct answer is ").append(correctAns).append("\n");
                        break;
                    }
                    default:
                        isCorrect = false;
                }
                if (isCorrect) correct++;
                qNum++;
            }
            // build results dialog with score and correct answers
            Dialog result = new Dialog("Results");
            Div container = new Div();
            container.add(new H3("Score: " + correct + "/" + total));
            container.add(new Pre(feedback.toString()));
            result.add(container);
            Button close = new Button("Close", ev -> result.close());
            HorizontalLayout rFooter = new HorizontalLayout(close);
            rFooter.setWidthFull();
            rFooter.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
            result.getFooter().add(rFooter);
            result.open();
        });

        VerticalLayout content = new VerticalLayout(title, questionsContainer);
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
    public void setPresenter(PublicPresenter presenter) {
        this.presenter = presenter;
    }
}




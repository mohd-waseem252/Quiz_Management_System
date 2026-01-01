package com.tachnique.app.quiz_ui.modules.admin;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.checkbox.Checkbox;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public class QuestionEditor extends Details {

    private final TextArea questionField = new TextArea("Question");
    private final ComboBox<String> typeCombo = new ComboBox<>("Question Type");
    private final VerticalLayout dynamicArea = new VerticalLayout();

    // MCQ
    private final List<TextField> mcqOptions = Arrays.asList(
            new TextField("Option 1"),
            new TextField("Option 2"),
            new TextField("Option 3"),
            new TextField("Option 4")
    );
    private final RadioButtonGroup<Integer> mcqCorrect = new RadioButtonGroup<>();

    // TRUE_FALSE
    private final RadioButtonGroup<Boolean> tfGroup = new RadioButtonGroup<>();

    // TEXT
    private final TextField textAnswer = new TextField("Answer");

    public QuestionEditor() {
        super();
        setSummaryText("Question");
        setOpened(false);

        questionField.setWidthFull();
        questionField.setRequiredIndicatorVisible(true);

        typeCombo.setItems("MCQ", "TRUE_FALSE", "TEXT");
        typeCombo.setWidthFull();
        typeCombo.setRequiredIndicatorVisible(true);
        typeCombo.addValueChangeListener(e -> renderDynamicArea());

        VerticalLayout content = new VerticalLayout(questionField, typeCombo, dynamicArea);
        content.setPadding(false);
        content.setSpacing(false);
        content.setWidthFull();
        setContent(content);

        renderDynamicArea();
    }

    private void renderDynamicArea() {
        dynamicArea.removeAll();
        String type = typeCombo.getValue();
        if (type == null) {
            return;
        }
        switch (type) {
            case "MCQ":
                renderMcq();
                break;
            case "TRUE_FALSE":
                renderTrueFalse();
                break;
            case "TEXT":
                renderText();
                break;
            default:
                // no-op
        }
    }

    private void renderMcq() {
        mcqCorrect.setLabel("Correct Option");
        mcqCorrect.setItems(1, 2, 3, 4);
        mcqCorrect.setRequired(true);
        mcqCorrect.setHelperText("Select which option is correct");

        FormLayout form = new FormLayout();
        mcqOptions.forEach(tf -> {
            tf.setWidthFull();
            tf.setRequiredIndicatorVisible(true);
            form.add(tf);
        });
        dynamicArea.add(form, mcqCorrect);
    }

    private void renderTrueFalse() {
        tfGroup.setLabel("Answer");
        tfGroup.setItems(Boolean.TRUE, Boolean.FALSE);
        tfGroup.setItemLabelGenerator(b -> b ? "True" : "False");
        tfGroup.setRequired(true);
        dynamicArea.add(tfGroup);
    }

    private void renderText() {
        textAnswer.setWidthFull();
        textAnswer.setRequiredIndicatorVisible(true);
        dynamicArea.add(textAnswer);
    }

    public boolean validate() {
        boolean baseValid = questionField.getValue() != null && !questionField.getValue().trim().isEmpty()
                && typeCombo.getValue() != null;
        if (!baseValid) {
            Notification.show("Question and type are required");
            return false;
        }
        switch (typeCombo.getValue()) {
            case "MCQ":
                boolean allFilled = mcqOptions.stream().allMatch(tf -> tf.getValue() != null && !tf.getValue().trim().isEmpty());
                boolean hasCorrect = mcqCorrect.getValue() != null;
                if (!allFilled || !hasCorrect) {
                    Notification.show("All four options and the correct selection are required");
                    return false;
                }
                return true;
            case "TRUE_FALSE":
                if (tfGroup.getValue() == null) {
                    Notification.show("Please select True or False");
                    return false;
                }
                return true;
            case "TEXT":
                if (textAnswer.getValue() == null || textAnswer.getValue().trim().isEmpty()) {
                    Notification.show("Answer is required");
                    return false;
                }
                return true;
            default:
                return false;
        }
    }

    // Simple getters to extract values when persisting
    public String getQuestionText() {
        return questionField.getValue();
    }

    public String getQuestionType() {
        return typeCombo.getValue();
    }

    public List<String> getMcqOptions() {
        return mcqOptions.stream().map(TextField::getValue).toList();
    }

    public Integer getMcqCorrectIndex() {
        return mcqCorrect.getValue();
    }

    public Boolean getTrueFalseAnswer() {
        return tfGroup.getValue();
    }

    public String getTextAnswer() {
        return textAnswer.getValue();
    }
}


package com.tachnique.app.quiz_ui.modules.auth;

import com.tachnique.app.dto.UserDto;
import com.tachnique.app.service.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Autowired;

@Route("signup")
@PageTitle("Sign Up")
public class SignupView extends VerticalLayout {

    private final UserService userService;

    @Autowired
    public SignupView(UserService userService) {
        this.userService = userService;
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        TextField username = new TextField("Username");
        PasswordField password = new PasswordField("Password");
        Checkbox isAdmin = new Checkbox("Is Admin");
        Button signup = new Button("Create Account");

        signup.addClickListener(e -> {
            if (username.isEmpty() || password.isEmpty()) {
                notifyCenter("Username and password are required");
                return;
            }
            try {
                UserDto dto = UserDto.builder()
                        .username(username.getValue())
                        .password(password.getValue())
                        .role(isAdmin.getValue() ? "ADMIN" : "PUBLIC")
                        .build();
                UserDto created = userService.createUser(dto);
                VaadinSession.getCurrent().setAttribute("user", created);
                if ("ADMIN".equalsIgnoreCase(created.getRole())) {
                    getUI().ifPresent(ui -> ui.navigate("admin"));
                } else {
                    getUI().ifPresent(ui -> ui.navigate("quiz"));
                }
            } catch (Exception ex) {
                notifyCenter("Signup failed: " + ex.getMessage());
            }
        });

        add(username, password, isAdmin, signup);
    }

    private void notifyCenter(String message) {
        Notification n = new Notification(message, 3000);
        n.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
        n.setPosition(Notification.Position.MIDDLE);
        n.open();
    }
}


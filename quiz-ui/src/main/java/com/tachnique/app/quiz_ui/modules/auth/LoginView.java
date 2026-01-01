package com.tachnique.app.quiz_ui.modules.auth;

import com.tachnique.app.dto.UserDto;
import com.tachnique.app.service.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Autowired;

@Route("login")
@PageTitle("Login")
public class LoginView extends VerticalLayout {

    private final UserService userService;

    @Autowired
    public LoginView(UserService userService) {
        this.userService = userService;
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        TextField username = new TextField("Username");
        PasswordField password = new PasswordField("Password");
        Button login = new Button("Login");
        Anchor signupLink = new Anchor("/signup", "Sign up");

        login.addClickListener(e -> {
            if (username.isEmpty() || password.isEmpty()) {
                notifyCenter("Username and password are required");
                return;
            }
            try {
                UserDto dto = new UserDto();
                dto.setUsername(username.getValue());
                dto.setPassword(password.getValue());
                UserDto logged = userService.login(dto);
                VaadinSession.getCurrent().setAttribute("user", logged);
                // navigate based on role
                if ("ADMIN".equalsIgnoreCase(logged.getRole())) {
                    getUI().ifPresent(ui -> ui.navigate("admin"));
                } else {
                    getUI().ifPresent(ui -> ui.navigate("quiz"));
                }
            } catch (Exception ex) {
                // if user not found, suggest signup
                notifyCenter("Login failed: " + ex.getMessage());
            }
        });

        add(username, password, login, signupLink);
    }

    private void notifyCenter(String message) {
        Notification n = new Notification(message, 3000);
        n.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
        n.setPosition(Notification.Position.MIDDLE);
        n.open();
    }
}


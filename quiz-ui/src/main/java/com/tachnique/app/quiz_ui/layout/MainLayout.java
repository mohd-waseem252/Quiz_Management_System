package com.tachnique.app.quiz_ui.layout;

import com.tachnique.app.dto.UserDto;
import com.tachnique.app.quiz_ui.modules.auth.SignupView;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinSession;

public class MainLayout extends AppLayout {

    private final HorizontalLayout header = new HorizontalLayout();

    public MainLayout() {
        setPrimarySection(Section.NAVBAR);
        buildHeader();
    }

    private void buildHeader() {
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        // Left area: app title + role-based links
        H1 title = new H1("Quiz App");
        HorizontalLayout leftArea = new HorizontalLayout(title);
        leftArea.setAlignItems(FlexComponent.Alignment.CENTER);

        // Right area: actions (logout or login/signup)
        HorizontalLayout rightArea = new HorizontalLayout();
        rightArea.setAlignItems(FlexComponent.Alignment.CENTER);

        UserDto user = (UserDto) VaadinSession.getCurrent().getAttribute("user");
        if (user != null) {
            // links based on role
            if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                leftArea.add(new RouterLink("Admin", com.tachnique.app.quiz_ui.modules.admin.AdminViewImpl.class));
                leftArea.add(new RouterLink("Public", com.tachnique.app.quiz_ui.modules.publicview.PublicViewImpl.class));
            } else {
                leftArea.add(new RouterLink("Public", com.tachnique.app.quiz_ui.modules.publicview.PublicViewImpl.class));
            }
            Button logout = new Button("Logout", e -> {
                VaadinSession.getCurrent().setAttribute("user", null);
                getUI().ifPresent(ui -> ui.navigate("login"));
            });
            rightArea.add(logout);
        } else {
            rightArea.add(new RouterLink("Login", com.tachnique.app.quiz_ui.modules.auth.LoginView.class));
            rightArea.add(new RouterLink("Sign Up", SignupView.class));
        }

        header.add(leftArea, rightArea);
        addToNavbar(header);
    }
}

package com.tachnique.app.quiz_ui;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.tachnique.app.quiz_ui.layout.MainLayout;
import com.vaadin.flow.server.VaadinSession;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Home")
public class HomeView extends VerticalLayout implements BeforeEnterObserver {
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        var user = (com.tachnique.app.dto.UserDto) VaadinSession.getCurrent().getAttribute("user");
        if (user == null) {
            event.forwardTo("login");
            return;
        }
        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            event.forwardTo("admin");
        } else {
            event.forwardTo("quiz");
        }
    }
}


package com.tachnique.app.quiz_service_impl.config;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.context.annotation.Configuration;

import com.tachnique.app.quiz_service_impl.serviceImpl.QuizServiceImpl;

@Configuration
public class JerseyConfig extends ResourceConfig {

    public JerseyConfig() {
//        packages("com.tachnique.app.quiz_service_impl.resource");
        register(JacksonFeature.class);
        register(QuizServiceImpl.class);
    }
}

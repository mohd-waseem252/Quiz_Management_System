package com.tachnique.app.quiz_ui.config;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.WebTarget;

import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.client.proxy.WebResourceFactory;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.tachnique.app.service.QuizService;

@Configuration
public class WebServiceConfig {

    @Value("${webservice.url.quiz}")
    private String quizWSUrl;

    @Bean
    public Client jerseyClient() {
        return JerseyClientBuilder.newBuilder().register(JacksonFeature.class)
                .build();
    }

    @Bean
    public QuizService quizService(Client jerseyClient) {
        WebTarget target = jerseyClient.target(quizWSUrl);
        return WebResourceFactory.newResource(QuizService.class, target);
    }
}

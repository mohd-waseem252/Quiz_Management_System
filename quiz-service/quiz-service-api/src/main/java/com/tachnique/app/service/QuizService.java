package com.tachnique.app.service;

import java.util.List;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import com.tachnique.app.dto.QuizDto;

@Path("/quizzes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface QuizService {

    @POST
    @Path("/create")
    QuizDto createQuiz(QuizDto quizDto);

    @GET
    @Path("/all")
    List<QuizDto> getQuizzes();
}

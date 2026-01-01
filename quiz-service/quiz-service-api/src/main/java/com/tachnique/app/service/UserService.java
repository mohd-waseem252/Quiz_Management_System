package com.tachnique.app.service;

import java.util.List;

import com.tachnique.app.dto.UserDto;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface UserService {

    @POST
    @Path("/create")
    UserDto createUser(UserDto userDto);

    @GET
    @Path("/all")
    List<UserDto> getUsers();

    @POST
    @Path("/login")
    UserDto login(UserDto credentials);
}

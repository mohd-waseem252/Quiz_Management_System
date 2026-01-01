package com.tachnique.app.dto;

import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Builder;
import lombok.AllArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class UserDto {
    private String role; // "ADMIN" or "PUBLIC"
    private String password;
    private String username;
    private Long id;
}






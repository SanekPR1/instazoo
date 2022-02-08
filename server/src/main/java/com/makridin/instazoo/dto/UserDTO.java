package com.makridin.instazoo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    Long id;
    @NotEmpty(message = "Username is required")
    String username;
    String email;
    @NotEmpty(message = "Firstname is required")
    String firstname;
    @NotEmpty(message = "Lastname is required")
    String lastname;
    String bio;

}

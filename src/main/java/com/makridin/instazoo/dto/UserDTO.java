package com.makridin.instazoo.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class UserDTO {
    Long id;
    @NotEmpty
    String username;
    String email;
    @NotEmpty
    String firstname;
    @NotEmpty
    String lastname;
    String bio;

}

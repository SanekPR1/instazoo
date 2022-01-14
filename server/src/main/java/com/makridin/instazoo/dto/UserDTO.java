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
    @NotEmpty
    String username;
    String email;
    @NotEmpty
    String firstname;
    @NotEmpty
    String lastname;
    String bio;

}

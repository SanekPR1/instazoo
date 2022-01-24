package com.makridin.instazoo.payload.request;

import com.makridin.instazoo.annotations.PasswordMatches;
import com.makridin.instazoo.annotations.UniqueEmail;
import com.makridin.instazoo.annotations.UniqueUsername;
import com.makridin.instazoo.annotations.ValidEmail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
@PasswordMatches
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignupRequest {

    @Email(message = "It should have email format")
    @NotBlank(message = "User email is required")
    @ValidEmail
    @UniqueEmail
    private String email;
    @NotEmpty(message = "Please enter your name")
    private String firstname;
    @NotEmpty(message = "Please enter your lastname")
    private String lastname;
    @NotEmpty(message = "Please enter your username")
    @UniqueUsername
    private String username;
    @NotEmpty(message = "Please enter your password ")
    @Length(min = 4, message = "Password minimum length is 4 chars")
    private String password;
    private String confirmPassword;
}

package com.makridin.instazoo.controllers;

import com.makridin.instazoo.payload.request.LoginRequest;
import com.makridin.instazoo.payload.request.SignupRequest;
import com.makridin.instazoo.payload.response.JWTTokenSuccessResponse;
import com.makridin.instazoo.payload.response.MessageResponse;
import com.makridin.instazoo.security.JWTTokenProvider;
import com.makridin.instazoo.security.SecurityConstants;
import com.makridin.instazoo.service.UserService;
import com.makridin.instazoo.validators.ResponseErrorValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/api/auth")
@PreAuthorize("permitAll()")
public class AuthController {

    @Autowired
    private UserService userService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JWTTokenProvider tokenProvider;

    @PostMapping("/signup")
    public ResponseEntity<Object> registerUser(
            @Valid @RequestBody SignupRequest request)
    {
        userService.createUser(request);
        return ResponseEntity.ok(new MessageResponse("User was registered successfully"));
    }


    @PostMapping("/signin")
    public ResponseEntity<Object> singIn (
            @Valid @RequestBody LoginRequest request)
    {
         Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = SecurityConstants.TOKEN_PREFIX + tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JWTTokenSuccessResponse(true, jwt));
    }
 }

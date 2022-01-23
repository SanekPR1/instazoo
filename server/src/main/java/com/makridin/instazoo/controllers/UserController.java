package com.makridin.instazoo.controllers;

import com.makridin.instazoo.dto.UserDTO;
import com.makridin.instazoo.service.UserService;
import com.makridin.instazoo.validators.ResponseErrorValidation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping("/api/user")
@CrossOrigin
public class UserController {

    private final UserService userService;
    private final ResponseErrorValidation errorValidation;

    @Autowired
    public UserController(UserService userService, ResponseErrorValidation errorValidation) {
        this.userService = userService;
        this.errorValidation = errorValidation;
    }

    @GetMapping("/")
    public ResponseEntity<UserDTO> getCurrentUser(Principal principal) {
        return ResponseEntity.ok(userService.getCurrentUser(principal));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable("userId") Long userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @PutMapping("/update")
    public ResponseEntity<Object> updateUser(
            @Valid @RequestBody UserDTO userDTO,
            Principal principal,
            BindingResult result
    ) {
        ResponseEntity<Object> errors = errorValidation.mapValidationService(result);
        if (!ObjectUtils.isEmpty(errors)) {
            return errors;
        }

        return ResponseEntity.ok(userService.updateUser(userDTO, principal));
    }
}

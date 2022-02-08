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

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
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
        return ResponseEntity.ok(userService.updateUser(userDTO, principal));
    }
}

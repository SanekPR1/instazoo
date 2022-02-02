package com.makridin.instazoo.validators;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@ResponseBody
@ControllerAdvice
public class ResponseErrorValidation {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> mapValidationService(BindingResult result) {
        if(result.hasErrors()) {
            Map<String, String> errorMap = new HashMap<>();
            if(!CollectionUtils.isEmpty(result.getAllErrors())) {
                result.getAllErrors().stream().forEach(error -> {
                    errorMap.put(error.getCode(), error.getDefaultMessage());
                });
            }

            result.getFieldErrors().stream().forEach(error -> {
                errorMap.put(error.getField(), error.getDefaultMessage());
            });

            return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
        }
        return null;
    }
}

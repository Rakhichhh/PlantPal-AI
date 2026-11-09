package com.plantpal.api;

import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import java.util.*;

@RestControllerAdvice
public class ApiErrorHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Map<String,String>> invalid(MethodArgumentNotValidException ex) {
        var e = ex.getBindingResult().getFieldErrors();
        String message = e.isEmpty() ? "Invalid input" : e.get(0).getField()+": "+e.get(0).getDefaultMessage();
        return ResponseEntity.badRequest().body(Map.of("error",message));
    }
    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<Map<String,String>> invalidBusiness(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("error",ex.getMessage()));
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<Map<String,String>> invalidJson(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body(Map.of("error","Malformed or unsupported JSON values"));
    }
}

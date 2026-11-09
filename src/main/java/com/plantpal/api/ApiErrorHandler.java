package com.plantpal.api;

import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import java.util.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

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
    @ExceptionHandler(ResponseStatusException.class)
    ResponseEntity<Map<String,String>> serviceError(ResponseStatusException ex){
        String msg=ex.getReason()==null?"Request failed":ex.getReason();
        return ResponseEntity.status(ex.getStatusCode()).body(Map.of("error",msg));
    }
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    ResponseEntity<Map<String,String>> tooBig(MaxUploadSizeExceededException ex){
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(Map.of("error","Maximum image size is 5 MB"));
    }
}

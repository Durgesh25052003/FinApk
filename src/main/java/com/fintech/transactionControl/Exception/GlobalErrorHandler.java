package com.fintech.transactionControl.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>>MethodArgumentNotFound(MethodArgumentNotValidException exception){
        Map<String,String>errorMessage=new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error-> errorMessage.put(error.getField(), error.getDefaultMessage()));
        return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RecordNotFound.class)
    public ResponseEntity<String> HandleUserNotFound(RecordNotFound exception){
        return new ResponseEntity<>(exception.getMessage(),HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ForbiddenAccess.class)
    public ResponseEntity<String>HandleForbiddenAccess(ForbiddenAccess forbiddenAccess){
        return new ResponseEntity<>(forbiddenAccess.getMessage(),HttpStatus.FORBIDDEN);
    }
}

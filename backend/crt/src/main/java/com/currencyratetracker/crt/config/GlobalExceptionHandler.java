package com.currencyratetracker.crt.config;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.currencyratetracker.crt.dtos.ErrorDto;
import com.currencyratetracker.crt.exceptions.AppException;

import jakarta.servlet.ServletException;

@ComponentScan
@ControllerAdvice
@Order(1)

public class GlobalExceptionHandler {


    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorDto> handleRuntimeException(RuntimeException ex) {
        System.out.println("RUNTIME EXCEPTION CAUGHT GLOBALLY");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorDto(ex.getMessage()));
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ErrorDto> handleTokenExpiredException(TokenExpiredException ex) {
        System.out.println("TokenExpiredException CAUGHT GLOBALLY");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorDto(ex.getMessage()));
    }

    @ExceptionHandler(ServletException.class)
    public ResponseEntity<ErrorDto> handleServletException(ServletException ex) {
        // Handle ServletException here
        System.out.println("SERVLET EXCEPTION CAUGHT GLOBALLY");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorDto(ex.getMessage()));
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorDto> handleUnauthorizedException(AppException ex) {
        
        System.out.println("APPEXCEPTION CAUGHT GLOBALLY: "  + ex);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorDto(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleException(Exception ex) {
        System.out.println("wah");
        // Handle the exception here, e.g., log it or return a custom error message.
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorDto(ex.getMessage()));
    }

    // You can add more exception handlers here for other custom exceptions

}

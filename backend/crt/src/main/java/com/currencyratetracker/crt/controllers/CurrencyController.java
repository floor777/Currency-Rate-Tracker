package com.currencyratetracker.crt.controllers;

import java.time.Instant;
import java.util.Arrays;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.exceptions.TokenExpiredException;

import java.util.List;

@RestController
public class CurrencyController {
    @GetMapping("/currency")
    public ResponseEntity <List<String>> messages() {
        try {
            System.out.println("try in currencycontroller.java");
            return ResponseEntity.ok(Arrays.asList("USD", "YEN"));
            
        } 
        catch (TokenExpiredException e) {
            Instant now = Instant.now();
        throw new TokenExpiredException("just testing tokenexpired", now);
        }
        catch (RuntimeException e) {
            System.out.println("runtimeexception error in currencycontroller.java");
            throw e;
        }


    }
    
}



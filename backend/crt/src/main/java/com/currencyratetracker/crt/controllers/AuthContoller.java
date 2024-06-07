package com.currencyratetracker.crt.controllers;

import java.net.URI;



import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.currencyratetracker.crt.config.UserAuthProvider;
import com.currencyratetracker.crt.dtos.CredentialsDto;
import com.currencyratetracker.crt.dtos.SignUpDto;
import com.currencyratetracker.crt.dtos.UserDto;
import com.currencyratetracker.crt.services.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;



@RestController
@RequiredArgsConstructor
public class AuthContoller {

    private final UserService userService;
    private final UserAuthProvider userAuthProvider;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody CredentialsDto credentialsDto, HttpServletResponse response) {

        System.out.println("/login in authcontroller.java");
        UserDto user = userService.login(credentialsDto);
        String accessToken = userAuthProvider.createAccessToken(user);
        String refreshToken = userAuthProvider.createRefreshToken(user);
        Cookie accessCookie = new Cookie("accessToken", accessToken);
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(false); // Use only for HTTPS
        accessCookie.setPath("/");
        accessCookie.setMaxAge(24 * 60 * 60); // 1 day
        response.addCookie(accessCookie);
        System.out.println("accessCookie added");

        Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(false); // Use only for HTTPS
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(24 * 60 * 60); // 1 day
        response.addCookie(refreshCookie);
        System.out.println("refreshCookie added");
        return ResponseEntity.ok("Login successful");

    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody SignUpDto signUpDto, HttpServletResponse response) {

            System.out.println("landed in authcontroller /register post");
            UserDto user = userService.register(signUpDto);
            System.out.println("in /register in AuthController.java");
            String accessToken = userAuthProvider.createAccessToken(user);
            String refreshToken = userAuthProvider.createRefreshToken(user);
            Cookie accessCookie = new Cookie("accessToken", accessToken);
            accessCookie.setHttpOnly(true);
            accessCookie.setSecure(false); // Use only for HTTPS
            accessCookie.setPath("/");
            accessCookie.setMaxAge(60); // seconds
            response.addCookie(accessCookie);

            Cookie refreshCookie = new Cookie("refreshToken", refreshToken);
            refreshCookie.setHttpOnly(true);
            refreshCookie.setSecure(false); // Use only for HTTPS
            refreshCookie.setPath("/");
            refreshCookie.setMaxAge(60); // seconds
            response.addCookie(refreshCookie);
            System.out.println("jwt token in here: " + accessToken);
            return ResponseEntity.created(URI.create("/users/" + user.getId())).body(user);

            

}
}

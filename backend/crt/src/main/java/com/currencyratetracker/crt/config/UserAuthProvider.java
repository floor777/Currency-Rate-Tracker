package com.currencyratetracker.crt.config;

import java.util.Date;
import java.util.Optional;
import java.time.Instant;
import java.util.Base64;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.currencyratetracker.crt.dtos.UserDto;
import com.currencyratetracker.crt.entities.User;
import com.currencyratetracker.crt.exceptions.AppException;
import com.currencyratetracker.crt.mappers.UserMapper;
import com.currencyratetracker.crt.repositories.UserRepository;
// import com.currencyratetracker.crt.services.ExceptionHandlingService;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
@Component
@ComponentScan
public class UserAuthProvider {

    @Value("${security.jwt.token.secret-key:secret-key}")
    private String secretKey;

    private UserRepository userRepository;

    private UserMapper userMapper;

    // private final ExceptionHandlingService exceptionHandlingService;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }
    @Autowired
    public UserAuthProvider(UserRepository userRepository, UserMapper userMapper
    //  ,ExceptionHandlingService exceptionHandlingService
     ) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        // this.exceptionHandlingService = exceptionHandlingService;
    }

    public String createAccessToken(UserDto userDto) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + 5000);
        String accessToken = JWT.create()
            .withIssuer("https://currencyratetracker.com")
            .withSubject(userDto.getId().toString())
            .withIssuedAt(now)
            .withExpiresAt(validity)
            .withClaim("id", userDto.getId())
            .withClaim("email", userDto.getLogin()) // Use email as a claim
            .withClaim("firstName", userDto.getFirstName())
            .withClaim("lastName", userDto.getLastName())
            .sign(Algorithm.HMAC256(secretKey));
        
        return accessToken;
    }

    public String createRefreshToken(UserDto userDto) {
        Date now = new Date();

        Date refreshValidity = new Date(now.getTime() + 10000);
        String refreshToken = JWT.create()
        .withIssuer("https://currencyratetracker.com")
        .withSubject(userDto.getId().toString())
        .withIssuedAt(now)
        .withExpiresAt(refreshValidity)
        
        .withClaim("email", userDto.getLogin()) // Use email as a claim
        .withClaim("firstName", userDto.getFirstName())
        .withClaim("lastName", userDto.getLastName())
        .sign(Algorithm.HMAC256(secretKey));
        
        return refreshToken;
    }

    public Authentication validateToken(String token) {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        JWTVerifier verifier = JWT.require(algorithm).build();

        DecodedJWT decoded = verifier.verify(token);

        UserDto user = UserDto.builder()
        .login(decoded.getIssuer())
        .firstName(decoded.getClaim("firstName").asString())
        .lastName(decoded.getClaim("lastName").asString())
        .build();

        return new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
    }

    public Authentication validateTokenStrongly(String token) {
        DecodedJWT decoded = null; // Declare decoded outside the try block
        
        try {
            System.out.println("inside try block in validateTokenStrongly");
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            JWTVerifier verifier = JWT.require(algorithm).build();
            decoded = verifier.verify(token); // Assign the value inside the try block
            System.out.println(decoded.getIssuer());
            System.out.println("decided payload: " + decoded.getSubject());
            
            
            User user = userRepository.findByLogin(decoded.getSubject())
            .orElseThrow(() -> new AppException("Unknown user", HttpStatus.NOT_FOUND));
    
            UserDto newUser = null;
            System.out.println("made it through try block verification worked");

            return new UsernamePasswordAuthenticationToken(userMapper.toUserDto(user), null, Collections.emptyList());
        } 
        // finally{
        //     System.out.println("finally block in userauthprovider");
        // }
        catch (TokenExpiredException e) {
            System.out.println("token expired exception caught in catch block in userauthprovider");

            
            throw e;
            // return new UsernamePasswordAuthenticationToken(null, null, Collections.emptyList());
        }
    }
    public boolean isValid(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT decoded = verifier.verify(token);
            return true;
        } catch (Exception e) {
            System.out.println("Error occurred in isValid. Likely expired token: " + e);
            return false;

        }
    }

}

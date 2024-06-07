package com.currencyratetracker.crt.config;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.ErrorResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.currencyratetracker.crt.dtos.UserDto;
import com.currencyratetracker.crt.exceptions.AppException;

import ch.qos.logback.core.subst.Token;
import jakarta.persistence.criteria.CriteriaBuilder.In;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor

public class JwtAuthFilter extends OncePerRequestFilter{

    private final UserAuthProvider userAuthProvider;
    
    
    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {
        String token = getAccessTokenFromRequest(request);
        System.out.println("request being preprocessed by jwtauthfilter");
        System.out.println("printing token from request");
        System.out.println(token);

    

        if (token != null) {
            try {
                System.out.println("starting try statement in jwtauthfilter");
                SecurityContextHolder.getContext().setAuthentication(userAuthProvider.validateTokenStrongly(token));
                System.out.println("No issue was found. Complete");
            } 
            catch(TokenExpiredException e) {
                System.out.println("Token was expired");
                System.out.println("Printing request path below");
                System.out.println(request.getRequestURL());
                if(request.getRequestURL().toString().equals("http://localhost:8080/register")
                 || request.getRequestURL().toString().equals("http://localhost:8080/login")) {
                    System.out.println(" it was a register or login it's ok");
                
                }
                else {
                    System.out.println("it wasnt a register or login. Checking if refresh token is still valid");
                    try {
                        // TODO. FIGURE OUT WHICH TO USE DIRECTLY DECODED AFTER VERIFYING OR IF IT'S POSSIBLE TO USE VERIFIED OBJECT
                        String refreshToken = getRefreshTokenFromRequest(request);
                        System.out.println("refreshToken: " + refreshToken);
                        if(userAuthProvider.isValid(refreshToken)) {
                            System.out.println("Refresh token was valid");
                            DecodedJWT test = JWT.decode(refreshToken);
                            System.out.println("decoded jwt: " + test);
                            System.out.println("decoded jwt payload: " + test.getClaims());
                            UserDto userDto = new UserDto(Long.valueOf(test.getSubject()), 
                            test.getClaim("firstName").asString(), test.getClaim("lastName").asString(), test.getClaim("email").asString());
                            String newAccessToken = userAuthProvider.createAccessToken(userDto);

                            Cookie accessCookie = new Cookie("accessToken", newAccessToken);
                            accessCookie.setHttpOnly(true);
                            accessCookie.setSecure(false); // Use only for HTTPS
                            accessCookie.setPath("/");
                            accessCookie.setMaxAge(60); // seconds
                            response.addCookie(accessCookie);
                            System.out.println("New cookie with new access token sent to frontend");
                            SecurityContextHolder.getContext().setAuthentication(userAuthProvider.validateTokenStrongly(newAccessToken));
                            // Continue with the filter chain
                            filterChain.doFilter(request, response);
                            return;
                            
                        }
                        else{
                            
                            System.out.println("Refresh token was invalid. Please login again");
                            throw new AppException("Refresh token was invalid. Please login again", HttpStatus.UNAUTHORIZED);
                        }
                        
                    } 
                    catch (TokenExpiredException refreshExpired) {
                        System.out.println("Refresh token was also invalid. Log in again");
                        throw new AppException("Refresh token was also invalid. Log in again ", HttpStatus.UNAUTHORIZED);
                    }
                    
                }
            }
            catch (RuntimeException e) {
                System.out.println("some other runtime exception below in jwtauthfilter");
                System.out.println(e.getMessage());
                throw new AppException("Some other runtime exception", HttpStatus.UNAUTHORIZED); // Throw authentication exception
            }
        

        }
        else {
            // If header is present but conditions are not met
            // Continue with filter chain execution
            filterChain.doFilter(request, response);
            return;
        }

       
        // If header is null
        // Continue with filter chain execution
        filterChain.doFilter(request, response);
    }

    private String getAccessTokenFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private String getRefreshTokenFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private boolean validateToken(String token) {
        // Implement token validation logic
        return true;
    }

    private Authentication getAuthentication(String token) {
        // Implement logic to retrieve authentication object from token
        return null;
    }




}

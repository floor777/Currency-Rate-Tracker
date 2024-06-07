package com.currencyratetracker.crt.services;

import java.nio.CharBuffer;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.currencyratetracker.crt.dtos.CredentialsDto;
import com.currencyratetracker.crt.dtos.SignUpDto;
import com.currencyratetracker.crt.dtos.UserDto;
import com.currencyratetracker.crt.entities.User;
import com.currencyratetracker.crt.mappers.UserMapper;
import com.currencyratetracker.crt.repositories.UserRepository;

import lombok.RequiredArgsConstructor;
import com.currencyratetracker.crt.exceptions.AppException;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    
    private final PasswordEncoder passwordEncoder;

    private final UserMapper userMapper;

    public UserDto login(CredentialsDto credentialsDto) {
        User user = userRepository.findByLogin(credentialsDto.login())
            .orElseThrow(() -> new AppException("Unknown user", HttpStatus.NOT_FOUND));

        if (passwordEncoder.matches(CharBuffer.wrap(credentialsDto.password()), user.getPassword())) {
            return userMapper.toUserDto(user);
            
        }
        throw new AppException("Invalid password", HttpStatus.BAD_REQUEST);
            

    };

    public UserDto register(SignUpDto signUpDto) {
        System.out.println("in register in UserService");
        Optional<User> oUser =  userRepository.findByLogin(signUpDto.login());

        if (oUser.isPresent()) {
            throw new AppException("Login already exists", HttpStatus.BAD_REQUEST);
        }

        User user = userMapper.signUpToUser(signUpDto);

        user.setPassword(passwordEncoder.encode(CharBuffer.wrap(signUpDto.password())));
        User savedUser = userRepository.save(user);
        
        System.out.println("savedUser: " +  savedUser);
        return userMapper.toUserDto(savedUser);
    }
    
}

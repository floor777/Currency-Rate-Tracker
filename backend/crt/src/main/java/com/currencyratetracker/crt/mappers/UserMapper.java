package com.currencyratetracker.crt.mappers;

import java.util.Optional;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.currencyratetracker.crt.dtos.SignUpDto;
import com.currencyratetracker.crt.dtos.UserDto;
import com.currencyratetracker.crt.entities.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toUserDto(User user);

    UserDto toUserDto(Optional<User> user);
    // ignore password because it has type char[] in record SignUpDto while it has type String in User class
    @Mapping(target = "password", ignore = true)
    User signUpToUser(SignUpDto signUpDto);
    
}

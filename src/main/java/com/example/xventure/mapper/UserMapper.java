package com.example.xventure.mapper;

import com.example.xventure.dto.UserDto;
import com.example.xventure.model.User;

public class UserMapper {

    public static User maptoUser(UserDto userDto){
        return new User(userDto.getId(), userDto.getUsername(), userDto.getEmail(), userDto.getRole());
    }

    public static UserDto maptoUserDto(User user){
        return new UserDto(user.getId(),user.getUsername(),user.getEmail(),user.getRole());
    }

    public static User createNewUser(UserDto userDto){
        return new User(userDto.getId(), userDto.getUsername(), userDto.getEmail(),userDto.getPassword(),userDto.getRole());
    }
}

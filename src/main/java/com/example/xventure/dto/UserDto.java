package com.example.xventure.dto;

import com.example.xventure.model.Role;
import com.example.xventure.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    private int id;
    private String username;
    private String email;
    private Role role;
    private String password;

    public UserDto(int id,String username,String email,Role role){
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
    }
}

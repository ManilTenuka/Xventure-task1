package com.example.xventure.dto;

import com.example.xventure.model.Permissions;
import com.example.xventure.model.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RolePermissionsDto {
    private int id;
    private Role  role;
    private Permissions permissions;
}

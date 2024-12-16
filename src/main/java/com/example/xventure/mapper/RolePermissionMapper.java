package com.example.xventure.mapper;

import com.example.xventure.dto.RolePermissionsDto;
import com.example.xventure.model.RolePermissions;

public class RolePermissionMapper {
    public static RolePermissions maptoRolePermissions(RolePermissionsDto rolePermissionsDto){
        return new RolePermissions(rolePermissionsDto.getId(),rolePermissionsDto.getRole(),rolePermissionsDto.getPermissions());
    }
}

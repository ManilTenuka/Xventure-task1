package com.example.xventure.service;

import com.example.xventure.dto.UserDto;
import com.example.xventure.model.Role;
import com.example.xventure.repository.RolePermissionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class ExceptionsService {

    @Autowired
    private RolePermissionsRepository rolePermissionsRepository;

    public boolean checkUserHasPermission(Role role, Integer permissionId){
        return rolePermissionsRepository.existsByRoleAndPermissions_Id(role,permissionId);
    }

    public boolean isUserInfoEmpty(UserDto userDto){
        if(userDto.getUsername().isEmpty() || userDto.getEmail().isEmpty() || userDto.getRole().name().isEmpty()){
            return true;
        }
        return false;
    }

    public boolean isValidEmail(String email){
        return email.matches("(?i).+" + "@" + ".+" + "." + ".+");
    }

    public String isValidPassword(String password){
        if(password.length()<8){
            return "Password too short";
        }
        else if(!password.matches(".*" + "[a-z]+" + ".*")){
            return "Password must contain at least one lower case letter";
        }
        else if(!password.matches(".*" + "[A-Z]+" + ".*")){
            return "Password must contain at least one upper case letter";
        }
        else if(!password.matches(".*" + "[0-9]+" + ".*")){
            return "Password must contain at least one number";
        }
        return "true";
    }

    public String checkUserHierachy(String editingRole, String currentRole){
        //editRole= The role which the crud operation is done
        //operationRole = The role who does the crud operation

        if(editingRole.equals("owner") && (currentRole.equals("admin") || currentRole.equals("auditor"))){
            if(currentRole.equals("admin")){
                return "Admins can't do operations on an owner";
            }
            else{
                return "Auditors can't do operations on an owner";
            }
        }
        else if(editingRole.equals("admin") && currentRole.equals("auditor")){


                return "Auditors can't do operations on an admin";

        }

        return "true";
    }



}

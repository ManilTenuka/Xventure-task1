package com.example.xventure.controller;

import com.example.xventure.dto.PasswordUpdateDto;
import com.example.xventure.dto.UserDto;
import com.example.xventure.mapper.UserMapper;
import com.example.xventure.model.Permissions;
import com.example.xventure.model.RolePermissions;
import com.example.xventure.model.User;
import com.example.xventure.service.AuthenticationService;
import com.example.xventure.service.CRUDService;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private CRUDService crudService;

    @Autowired
    private AuthenticationService authenticationService;

    @GetMapping("/viewPermissions")
    public ResponseEntity<?> viewPermissions(){
        return crudService.viewPermissions() ;
    }

    @GetMapping("/viewUsers")
    public ResponseEntity<?> viewUsers(){
        return crudService.viewUsers() ;
    }

    @GetMapping("/searchUser")
    public ResponseEntity<?> searchUser(
            @RequestParam(defaultValue = "username") String type, //search by username or email
            @RequestParam(defaultValue = "any" ) String index ,  //search by any position(value="any") or search from the zeroth index(value = "start").
            @RequestParam String subString
    )
    {
        return crudService.searchUsers(type,index,subString);
    }

    @PostMapping("/createUser")
    public ResponseEntity<?> createUser(@RequestBody UserDto userDto){
        return crudService.createUsers(userDto);
    }

    @DeleteMapping("/deleteUser/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id){
        return crudService.deleteUsers(id);
    }

    @PutMapping("updateUser/{id}/{username}")
    public ResponseEntity<?> updateUserName(@PathVariable Integer id,@PathVariable String username){
        return crudService.updateUsername(id , username);
    }

    @PutMapping("updatePassword")
    public ResponseEntity<?> updatePassword(@RequestBody PasswordUpdateDto passwordUpdateDto){
        return crudService.updatePassword(passwordUpdateDto.getOldPassword(),passwordUpdateDto.getNewPassword());
    }

    @PutMapping("authorizePermission/{role}/{permissionId}")
    public ResponseEntity<?> authorizePermission(@PathVariable String role,@PathVariable Integer permissionId){
        return crudService.authorizePermissions(role , permissionId);
    }


    @PutMapping("deletePermission/{role}/{permissionId}")
    public ResponseEntity<?> deletePermission(@PathVariable String role,@PathVariable Integer permissionId){
        return crudService.deletePermissions(role,permissionId);
    }
}

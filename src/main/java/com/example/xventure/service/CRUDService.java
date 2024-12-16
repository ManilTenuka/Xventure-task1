package com.example.xventure.service;

import com.example.xventure.dto.RolePermissionsDto;
import com.example.xventure.dto.UserDto;
import com.example.xventure.mapper.UserMapper;
import com.example.xventure.model.Permissions;
import com.example.xventure.model.Role;
import com.example.xventure.model.RolePermissions;
import com.example.xventure.model.User;
import com.example.xventure.repository.PermissionsRepository;
import com.example.xventure.repository.RolePermissionsRepository;
import com.example.xventure.repository.UserRepository;
import com.example.xventure.utils.PermissionIds;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.beans.Encoder;
import java.util.*;

@Service
public class CRUDService {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RolePermissionsRepository rolePermissionsRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ExceptionsService exceptionsService;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private PermissionsRepository permissionsRepository;


    public ResponseEntity<?> viewPermissions(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication==null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You don't have access. Try Login again");
        }
        Role roleName = Role.valueOf(authentication.getAuthorities().iterator().next().getAuthority());

        if(!exceptionsService.checkUserHasPermission(roleName, PermissionIds.viewPermissions)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You don't have permission to View Permissions.");
        }; //checks whether the user has permission

        Map<String,List<String>> mapRolePermissions = new HashMap<>(); //to show the role and its permission in the more structured way
        List<String> ownerPermissionList = new ArrayList<>();
        List<String> adminPermissionList = new ArrayList<>();
        List<String> auditorPermissionList = new ArrayList<>(); //three lists to store the permissions of each role

        rolePermissionsRepository.findAll().forEach(rolePermissions -> {
            if(rolePermissions.getRole().name().equals("owner")){
                ownerPermissionList.add(rolePermissions.getPermissions().getName());
            }
            else if(rolePermissions.getRole().name().equals("admin")){
                adminPermissionList.add(rolePermissions.getPermissions().getName());
            }
            else if(rolePermissions.getRole().name().equals("auditor")){
                auditorPermissionList.add(rolePermissions.getPermissions().getName());
            }
        });//stores the permissions by iterating through all permissions in rolePermission table

        mapRolePermissions.put("owner",ownerPermissionList);
        mapRolePermissions.put("admin",adminPermissionList);
        mapRolePermissions.put("auditor",auditorPermissionList); //put the roles as key  and the lists as its value

        return ResponseEntity.ok(mapRolePermissions); //return the map
    }

    public ResponseEntity<?> viewUsers(){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication==null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You don't have access. Try Login again");
        }
        Role roleName = Role.valueOf(authentication.getAuthorities().iterator().next().getAuthority());

        if(!exceptionsService.checkUserHasPermission(roleName, PermissionIds.viewUser)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You don't have permission to View Users.");
        };

        List<User> users = userRepository.findAll();


        return ResponseEntity.ok(users.stream().map(UserMapper::maptoUserDto).toList());
    }

    public ResponseEntity<?> searchUsers(String type, String index, String subString){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication==null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You don't have access. Try Login again");
        }
        Role roleName = Role.valueOf(authentication.getAuthorities().iterator().next().getAuthority());

        if(!exceptionsService.checkUserHasPermission(roleName, PermissionIds.searchUser)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You don't have permission to search Users.");
        };

        if(!(type.equalsIgnoreCase("username") || type.equalsIgnoreCase("email"))){ //to check whether the username is given correctly.
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please Enter either 'username' or 'email' type to continue. ");
        }//first 3 if conditions checks where RequestParams are given properly in the url

        if(!(index.equalsIgnoreCase("any") || index.equalsIgnoreCase("start"))){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please Enter either 'any' or 'start' for the index to continue. ");
        }

        if(subString.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please Enter a subString ");
        }


        List<User> users = userRepository.findAll() ;
        if(type.equalsIgnoreCase("username")){

            if(index.equalsIgnoreCase("any")){ //filters the stream using regex and returns if the substring appears somewhere in username
                return ResponseEntity.ok( users.stream().filter(user->user.getUsername().matches("(?i).*" + subString + ".*")).map(UserMapper::maptoUserDto).toList());
            }

            else if(index.equalsIgnoreCase("start")){//same as above but returns if the substring appear only in the beginning
                return ResponseEntity.ok( users.stream().filter(user->user.getUsername().matches("(?i)" + subString + ".*")).map(UserMapper::maptoUserDto).toList());
            }
        }
        else if(type.equalsIgnoreCase("email")){

            if(index.equalsIgnoreCase("any")){ //filters the stream using regex and returns if the substring appears somewhere in email
                return ResponseEntity.ok( users.stream().filter(user->user.getEmail().matches("(?i).*" + subString + ".*")).map(UserMapper::maptoUserDto).toList());
            }

            else if(index.equalsIgnoreCase("start")){//same as above but returns if the substring appear only in the beginning
                return ResponseEntity.ok( users.stream().filter(user->user.getEmail().matches("(?i)" + subString + ".*")).map(UserMapper::maptoUserDto).toList());
            }
        }

        return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Search Failed ");

    }

    public ResponseEntity<?> createUsers(UserDto userDto){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication==null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You don't have access. Try Login again");
        }
        Role roleName = Role.valueOf(authentication.getAuthorities().iterator().next().getAuthority());

        if(!exceptionsService.checkUserHasPermission(roleName, PermissionIds.createUser)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You don't have permission to create Users.");
        };

        String roleHierachy = exceptionsService.checkUserHierachy(userDto.getRole().toString(),authentication.getAuthorities().iterator().next().getAuthority()); //checks whether a user has the power to do the operation
        if(!roleHierachy.equals("true")){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(roleHierachy);
        }


        String currentRole = authentication.getAuthorities().iterator().next().getAuthority(); //to get the current userData
        String newRole = userDto.getRole().name();


        //An Owner cannot be created again.
        //An Admin can only be created by an owner
        //An Auditor can be created by an owner or an admin
        //other logics are checked through authentication service

        if(newRole.equals("owner")){
           return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You cannot create owner");
        }
        else if (newRole.equals("admin")){
            if(currentRole.equals("auditor")){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Auditors can't create admins");
            }
        }

        return authenticationService.register(UserMapper.createNewUser(userDto));
    }

    public ResponseEntity<?> deleteUsers(Integer userID){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication==null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You don't have access. Try Login again");
        }
        Role roleName = Role.valueOf(authentication.getAuthorities().iterator().next().getAuthority());


        if(!exceptionsService.checkUserHasPermission(roleName, PermissionIds.deleteUser)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You don't have permission to delete Users.");
        };

        String roleHierachy = exceptionsService.checkUserHierachy(userRepository.findById(userID).get().getRole().toString(),authentication.getAuthorities().iterator().next().getAuthority()); //checks whether a user has the power to do the operation
        if(!roleHierachy.equals("true")){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(roleHierachy);
        }


        //checks whether the user exists
        if(userRepository.findById(userID).isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User Has been already Deleted");

        }

        userRepository.deleteById(userID);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body("User deleted successfully");
    }

    public ResponseEntity<?> updateUsername(String username){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication==null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You don't have access. Try Login again");
        }
        Role roleName = Role.valueOf(authentication.getAuthorities().iterator().next().getAuthority());
        String userEmail = authentication.getName();

        if(!exceptionsService.checkUserHasPermission(roleName, PermissionIds.updateUser)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You don't have permission to update Users.");
        };



        if( username==null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please make sure to give a id and a username");

        }

        Optional<User> user = userRepository.findByEmail(userEmail);


        if(user.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User Doesn't Exist");
        }


        String roleHierachy = exceptionsService.checkUserHierachy(user.get().getRole().name(),authentication.getAuthorities().iterator().next().getAuthority()); //checks whether a user has the power to do the operation
        if(!roleHierachy.equals("true")){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(roleHierachy);
        }

        if(username.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("user name Can't be Empty");
        }

        user.get().setUsername(username);
        userRepository.save(user.get());


        return ResponseEntity.status(HttpStatus.OK).body(UserMapper.maptoUserDto(user.get()));

    }

    public ResponseEntity<?> updatePassword(String oldPassword, String newPassword){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication==null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You don't have access. Try Login again");
        }
        Role roleName = Role.valueOf(authentication.getAuthorities().iterator().next().getAuthority());

        String userEmail = authentication.getName();
        Optional<User> user = userRepository.findByEmail(userEmail);
        if(user.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        if(!passwordEncoder.matches(oldPassword,user.get().getPassword())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Passwords do not match");
        }

        String passwordValidity = exceptionsService.isValidPassword(newPassword);
        if(!passwordValidity.equals("true")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(passwordValidity);
        }


        String newHashedPassword = passwordEncoder.encode(newPassword);
        user.get().setPassword(newHashedPassword);
        userRepository.save(user.get());
        return ResponseEntity.status(HttpStatus.OK).body("Password is updated successfully");



    }

    public ResponseEntity<?> authorizePermissions(String role, int PermissionId){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication==null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You don't have access. Try Login again");
        }
        Role roleName = Role.valueOf(authentication.getAuthorities().iterator().next().getAuthority());


        Optional<Permissions> permissions = permissionsRepository.findById(PermissionId);


        if(!(role.equals("owner") || role.equals("admin") || role.equals("auditor"))){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No such role is found");
        }
        if(!exceptionsService.checkUserHasPermission(roleName, PermissionIds.updatePermissions)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You don't have permission to update Permissions.");
        };

        String roleHierachy = exceptionsService.checkUserHierachy(role,authentication.getAuthorities().iterator().next().getAuthority()); //checks whether a user has the power to do the operation
        if(!roleHierachy.equals("true")){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(roleHierachy);
        }


        if(permissions.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No such permission is found");
        }

        if(exceptionsService.checkUserHasPermission(Role.valueOf(role), PermissionId)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(role + " has been given access to " + permissions.get().getName() + " already.");
        };

        RolePermissions rolePermissions = new RolePermissions(Role.valueOf(role),permissions.get());
        rolePermissionsRepository.save(rolePermissions);
        return ResponseEntity.status(HttpStatus.OK).body("Updated Successfully\n" + rolePermissions);
    }

    @Transactional
    public ResponseEntity<?> deletePermissions(String role, int PermissionId){

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication==null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You don't have access. Try Login again");
        }
        Role roleName = Role.valueOf(authentication.getAuthorities().iterator().next().getAuthority());

        Optional<Permissions> permissions = permissionsRepository.findById(PermissionId);


        if(!(role.equals("owner") || role.equals("admin") || role.equals("auditor"))){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No such role is found");
        }
        if(!exceptionsService.checkUserHasPermission(roleName, PermissionIds.updatePermissions)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You don't have permission to delete Permissions.");
        };

        String roleHierachy = exceptionsService.checkUserHierachy(role,authentication.getAuthorities().iterator().next().getAuthority()); //checks whether a user has the power to do the operation
        if(!roleHierachy.equals("true")){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(roleHierachy);
        }


        if(permissions.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No such permission is found");
        }

        if(!exceptionsService.checkUserHasPermission(Role.valueOf(role), PermissionId)){
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(role + " has not been given " + permissions.get().getName() + " permission to delete." );
        };

        rolePermissionsRepository.deleteByRoleAndPermissions_Id(Role.valueOf(role),permissions.get().getId());
        return ResponseEntity.status(HttpStatus.OK).body("role: [" + role + "] permission: [" + permissions.get().getName() + "] Deleted Successfully\n" );
    }

}

package com.example.xventure.controller;



import com.example.xventure.dto.UserDto;
import com.example.xventure.model.AuthenticationResponse;
import com.example.xventure.model.User;
import com.example.xventure.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin("*")
@RestController
public class AuthenticationController {
    private final AuthenticationService authservice;

    public AuthenticationController(AuthenticationService authservice) {
        this.authservice = authservice;
    }

//    @PostMapping("/signup")
//    public ResponseEntity<?> register(@RequestBody User request) {
//        return authservice.register(request);
//    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody User request) {
        System.out.println("Reached Login");
        return ResponseEntity.ok(authservice.authenticate(request));
    }
}


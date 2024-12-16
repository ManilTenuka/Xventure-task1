package com.example.xventure.service;



import com.example.xventure.dto.UserDto;
import com.example.xventure.mapper.UserMapper;
import com.example.xventure.model.AuthenticationResponse;
import com.example.xventure.model.User;
import com.example.xventure.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    @Autowired
    private ExceptionsService exceptionsService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public ResponseEntity<?> register(User request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("User has already Registered.");
        }

        if(userRepository.existsByUsername(request.getUsername())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username Already Exists");
        }

        //checks whether all rows are filled
        if(exceptionsService.isUserInfoEmpty(UserMapper.maptoUserDto(request))){
            return ResponseEntity.badRequest().body("All rows must be filled.");
        }

        if(!exceptionsService.isValidEmail(request.getEmail())){
            return ResponseEntity.badRequest().body("Please use a valid email. ");
        }

        String passwordValidityResponse = exceptionsService.isValidPassword(request.getPassword());
        if(!passwordValidityResponse.equals("true")){
            return ResponseEntity.badRequest().body(passwordValidityResponse);
        }


        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        user = userRepository.save(user);

        String token = jwtService.generateToken(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(UserMapper.maptoUserDto(user));
    }

    public AuthenticationResponse authenticate(User request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            // If authentication succeeds, continue with the logic
            User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
            String token = jwtService.generateToken(user);
            return new AuthenticationResponse(token);
        } catch (AuthenticationException e) {
            System.out.println("Authentication failed: " + e.getMessage());
            throw new RuntimeException("Authentication failed: " + e.getMessage());
        }
    }

}

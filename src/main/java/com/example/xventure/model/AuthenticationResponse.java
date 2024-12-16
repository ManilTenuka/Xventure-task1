package com.example.xventure.model;

public class AuthenticationResponse {
    private final String token;

    public AuthenticationResponse(String token) {
        this.token = token;
    }

    public String getJwt() {
        return token;
    }
}


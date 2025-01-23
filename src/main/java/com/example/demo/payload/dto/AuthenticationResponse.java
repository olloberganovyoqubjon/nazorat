package com.example.demo.payload.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthenticationResponse {
    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("message")
    private String message;


    public AuthenticationResponse() {
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public AuthenticationResponse(String accessToken, String message) {
        this.accessToken = accessToken;
        this.message = message;
    }

    public String getAccessToken() {
        return accessToken;
    }


    public String getMessage() {
        return message;
    }

}

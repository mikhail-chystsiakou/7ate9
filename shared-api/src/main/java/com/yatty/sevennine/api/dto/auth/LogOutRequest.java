package com.yatty.sevennine.api.dto.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LogOutRequest {
    private String authToken;
    
    public LogOutRequest() {
    }
    
    public LogOutRequest(String authToken) {
        this.authToken = authToken;
    }
    
    public String getAuthToken() {
        return authToken;
    }
    
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}

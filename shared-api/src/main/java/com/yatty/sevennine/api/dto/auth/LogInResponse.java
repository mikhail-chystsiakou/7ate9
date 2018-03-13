package com.yatty.sevennine.api.dto.auth;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LogInResponse {
    private boolean succeed;
    private String authToken;
    private String description;
    
    public boolean isSucceed() {
        return succeed;
    }
    
    public void setSucceed(boolean succeed) {
        this.succeed = succeed;
    }
    
    public String getAuthToken() {
        return authToken;
    }
    
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}

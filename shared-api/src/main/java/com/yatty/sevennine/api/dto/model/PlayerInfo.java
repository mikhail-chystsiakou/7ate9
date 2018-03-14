package com.yatty.sevennine.api.dto.model;

public class PlayerInfo {
    private String name;
    
    public PlayerInfo() {
    
    }
    
    public PlayerInfo(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
}

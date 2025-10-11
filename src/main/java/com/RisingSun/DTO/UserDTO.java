package com.RisingSun.DTO;

public class UserDTO{
    private String username;
    private String name;

    public UserDTO(String username, String name) {
        this.username = username;
        this.name = name;
    }

    public String getUsername() { return username; }
    public String getName() { return name; }
}

package com.cac.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginDetails {

    @NotBlank
    private String username;
    @NotBlank
    private String password;
    private String role;
    

    public LoginDetails(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

}

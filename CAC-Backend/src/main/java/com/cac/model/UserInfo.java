package com.cac.model;

import jakarta.annotation.Generated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class UserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 50, nullable = false)
    private String username;

    @Column(length=50, nullable = false)
    private String password;

    @Column(nullable = false, length = 10)
    private String role;

    public UserInfo(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }
}

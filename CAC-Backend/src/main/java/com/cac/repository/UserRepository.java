package com.cac.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cac.model.UserInfo;

@Repository
public interface UserRepository extends JpaRepository<UserInfo, Integer>{

    Optional<UserInfo> findByUsername(String username);

    UserInfo findByUsernameAndPassword(String username, String password);

    UserInfo findByUsernameAndPasswordAndRoleIgnoreCase(String username, String password, String role);
    
} 

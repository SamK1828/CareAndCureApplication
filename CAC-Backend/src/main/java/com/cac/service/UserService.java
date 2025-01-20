package com.cac.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cac.exception.UserNotFoundException;
import com.cac.dto.LoginDetails;
import com.cac.model.UserInfo;
import com.cac.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UserInfo createUser(UserInfo userInfo) throws UserNotFoundException {
        userInfo.setRole(userInfo.getRole().toUpperCase());
        UserInfo savedUserInfo = null;
        try{
        userRepository.save(userInfo);
        } catch(Exception e){
            throw new UserNotFoundException("User already exist with username: "+userInfo.getUsername());
        }
        return  savedUserInfo;
    }


    public UserInfo getUserByUsername(String username) throws UserNotFoundException {
        UserInfo userData = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username:" + username));
        return userData;
    }

    public UserInfo verifyLoginDetails(LoginDetails loginDetails) throws UserNotFoundException {

        // UserInfo userInfo = userRepository.findByUsernameAndPassword(loginDetails.getUsername(), loginDetails.getPassword());
        UserInfo userInfo = userRepository.findByUsernameAndPasswordAndRoleIgnoreCase(loginDetails.getUsername(), loginDetails.getPassword(), loginDetails.getRole());

        if(userInfo!=null) return userInfo;
        else {
            throw new UserNotFoundException("Invalid username or password. Try again!");
        }
    }

    public void deleteUser(UserInfo userInfo) {
        userRepository.delete(userInfo);
    }

}

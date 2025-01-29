package com.cac.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cac.exception.UserNotFoundException;
import com.cac.dto.LoginDetails;
import com.cac.model.Patient;
import com.cac.model.UserInfo;
import com.cac.repository.PatientRepository;
import com.cac.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PatientRepository patientRepository;

    public UserInfo createUser(UserInfo userInfo) throws UserNotFoundException {
        userInfo.setRole(userInfo.getRole().toUpperCase());
        UserInfo savedUserInfo = null;
        try{
        savedUserInfo = userRepository.save(userInfo);
        } catch(Exception e){
            throw new UserNotFoundException("Failed to register");
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

        if(userInfo!=null) {
            if(loginDetails.getRole().equalsIgnoreCase("patient")){
                Patient patient = patientRepository.findById(Integer.parseInt(loginDetails.getUsername())).orElseThrow(()->new UserNotFoundException("Patient Not Found with Id : "+ loginDetails.getUsername()));
                if(!patient.isActive()) throw new UserNotFoundException("Your account is deactivated .Please contact to Care and Cure.");
            } 

            return userInfo;
        }
        else {
            throw new UserNotFoundException("Invalid username or password. Try again!");
        }
    }

    public void deleteUser(UserInfo userInfo) {
        userRepository.delete(userInfo);
    }

}

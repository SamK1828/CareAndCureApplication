package com.cac.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Service;

import com.cac.exception.UserNotFoundException;
import com.cac.model.AdminInfo;
import com.cac.dto.LoginDetails;
import com.cac.model.UserInfo;
import com.cac.repository.AdminRepository;

import jakarta.mail.MessagingException;

@Service
public class AdminService {
    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private UserService userService;

    public UserInfo loginAdmin(String username, String password) throws UserNotFoundException {
        // AdminInfo adminInfo = adminRepository.findByUsername(username).orElse(()->UserNotFoundException("Admin "));
        return userService.verifyLoginDetails(new LoginDetails(username, password, "admin"));
        
    }

    // Method to update admin details
    public AdminInfo updateAdmin(int id, AdminInfo updatedAdminInfo) throws Exception {
        AdminInfo existingAdmin = adminRepository.findById(id)
                .orElseThrow(() -> new Exception("Admin not found"));

        existingAdmin.setName(updatedAdminInfo.getName());
        existingAdmin.setUsername(updatedAdminInfo.getUsername());
        existingAdmin.setEmail(updatedAdminInfo.getEmail());

        return adminRepository.save(existingAdmin);
    }

    public AdminInfo getAdminInfo(String username) throws UserNotFoundException{
        return adminRepository.findByUsername(username).orElseThrow(()-> new UserNotFoundException("Admin Not Foud with username: "+ username));
    }

}

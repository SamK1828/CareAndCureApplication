package com.cac.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cac.exception.UserNotFoundException;
import com.cac.model.AdminInfo;
import com.cac.service.AdminService;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    // Update Admin details
    @PutMapping("/updateAdmin/{id}")
    public ResponseEntity<AdminInfo> updateAdmin(@PathVariable int id, @Valid @RequestBody AdminInfo adminInfo)
            throws Exception {
        return new ResponseEntity<AdminInfo>(adminService.updateAdmin(id, adminInfo), HttpStatus.ACCEPTED);
    }

    @GetMapping("/viewAdminInfo/{username}")
    public ResponseEntity<AdminInfo> getAdminInfo(@PathVariable String username) throws UserNotFoundException {
        AdminInfo adminInfo = adminService.getAdminInfo(username);
        return new ResponseEntity<>(adminInfo, HttpStatus.OK);
    }

    
}

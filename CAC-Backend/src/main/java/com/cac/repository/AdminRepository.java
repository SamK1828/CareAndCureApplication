package com.cac.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cac.model.AdminInfo;

@Repository
public interface AdminRepository extends JpaRepository<AdminInfo, Integer> {

    Optional<AdminInfo> findByUsername(String username); 

}

package com.cac.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class DoctorDTO {
    private int doctorId;
    private String doctorName;
    private String specialization;
    private String qualification;
    private String contactNumber;
    private String emailId;
    private String gender;
    private String location;
    private double consultationFees;
    private LocalDate dateOfJoining;
    private Boolean isSurgeon;
    private int yearsOfExperience;
    private Boolean status;

    public DoctorDTO(int id, String doctorName){
        this.doctorId=id;
        this.doctorName=doctorName;

    }
}
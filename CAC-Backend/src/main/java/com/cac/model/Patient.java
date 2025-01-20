package com.cac.model;

// import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "patient")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int patientId;

    @Column(name = "patientName", nullable = false, length = 50)
    private String patientName;

    private Integer age;
    private LocalDate dateOfBirth;

    @Column(length = 10)
    private String gender;

    @Column(unique = true, length = 15)
    private String contactNumber;

    @Column(length = 100)
    private String address;

    @Column(unique = true, length = 100)
    private String emailId;

    @Lob
    private String medicalHistory;

    @Lob
    private String allergies;

    @Lob
    private String medications;

    private String insuranceDetails;
    @Lob
    private String treatments;

    private Boolean isActive;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @JsonBackReference
    @JsonIgnore
    private List<Appointment> appointments;

    public String getEmail() {
        return this.emailId;
    }

    public void setActive(boolean active){
        this.isActive=active;
    }

    public boolean isActive(){
        return this.isActive;
    }
}



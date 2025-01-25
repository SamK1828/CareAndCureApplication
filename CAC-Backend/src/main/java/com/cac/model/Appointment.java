package com.cac.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "appointment")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Appointment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int appointmentId;

  private LocalDate appointmentDate;
  private LocalTime appointmentTime;
  private String status="Scheduled";
  @Size(min = 5, message = "Reason must be at least 10 characters long")
  private String reason;
  
  private String reasonOfCancellation;

  @ManyToOne(fetch = FetchType.EAGER) 
  @JoinColumn(name = "patient_id", nullable = false)
  private Patient patient;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "doctor_id", nullable = false)
  @JsonIgnore
  private Doctor doctor;

  @Transient
  private String doctorName;

  @Transient
  private Integer doctorId;
  @Transient
  private String doctorPhoneNumber;
  @Transient
  private String specialty;

  @PostLoad
  public void setDoctorNameAndId() {
    if (doctor != null) {
      this.doctorPhoneNumber=doctor.getContactNumber();
      this.specialty=doctor.getSpecialty();
      this.doctorName = doctor.getName();  // Assuming Doctor entity has getName()
      this.doctorId = doctor.getDoctorId();  // Assuming Doctor entity has getId()
    }
  }
  public  void  setMyStatues(String s){
    this.status=s;
  }

}


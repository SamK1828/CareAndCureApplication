package com.cac.model;

// import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.*;
import com.cac.annotations.ValidDateOfBirth;
import java.time.LocalDate;
import java.util.List;

@ValidDateOfBirth
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
    @NotBlank(message = "Patient Name is mandatory")
    private String patientName;

    @Positive(message = "Age must be required")
    private Integer age;
    private LocalDate dateOfBirth;

    @Column(length = 10)
    @NotBlank(message = "Select gender")
    private String gender;

    // @Column(unique = true, length = 15)
    @NotBlank(message = "Contact Number is mandatory")
    @Column(nullable = false)
    @Pattern(regexp = "^\\+[1-9]{1}[0-9]{0,2}[0-9]{10}$", message = "Contact number must start with a country code (e.g., +1) followed by a 10-digit mobile number")
    private String contactNumber;

    @Column(length = 100)
    @NotBlank(message = "Address required!")
    private String address;

    @Email(message = "Enter valid email", regexp = "^[a-zA-Z0-9._-]+@[a-zA-Z0-9]+\\.[a-zA-Z]{2,}$")
    @Column(unique = true)

    // @Column(unique = true, length = 100)
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
    // @JsonBackReference
    @JsonIgnore
    private List<Appointment> appointments;

    public String getEmail() {
        return this.emailId;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }

    public boolean isActive() {
        return this.isActive;
    }
}

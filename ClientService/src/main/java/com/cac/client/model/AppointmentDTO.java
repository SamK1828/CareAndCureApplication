package com.cac.client.model;

public class AppointmentDTO {
    private int appointmentId;
    private int patientId;
    private Patient patient;
    private String appointmentDate;
    private String appointmentTime;
    private String status;
    private int doctorId;
    private String reason;
    private String reasonOfCancellation;
    private String doctorName;
    public String getReason() {
		return reason;
	}
	public AppointmentDTO(int appointmentId, int patientId, String appointmentDate, String appointmentTime,
            String status, int doctorId, String reason, String reasonOfCancellation, String doctorName) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.status = status;
        this.doctorId = doctorId;
        this.reason = reason;
        this.reasonOfCancellation = reasonOfCancellation;
        this.doctorName = doctorName;
    }
    public String getDoctorName() {
        return doctorName;
    }
    public void setReason(String reason) {
		this.reason = reason;
	}
	public String getReasonOfCancellation() {
		return reasonOfCancellation;
	}
	public void setReasonOfCancellation(String reasonOfCancellation) {
		this.reasonOfCancellation = reasonOfCancellation;
	}
	public int getDoctorId() {
        return doctorId;
    }
    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }
    
    public int getAppointmentId() {
        return appointmentId;
    }
    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }
    public int getPatientId() {
        return patientId;
    }
    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }
    public String getAppointmentDate() {
        return appointmentDate;
    }
    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }
    public String getAppointmentTime() {
        return appointmentTime;
    }
    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public Patient getPatient() {
        return patient;
    }
    public void setPatient(Patient patient) {
        this.patient = patient;
    }
    
}

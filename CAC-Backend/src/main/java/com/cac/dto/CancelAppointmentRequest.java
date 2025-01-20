package com.cac.dto;

public class CancelAppointmentRequest {
    private int appointmentId;
    private String reasonOfCancellation;

    // Constructors
    public CancelAppointmentRequest() {}

    public CancelAppointmentRequest(int appointmentId, String reasonOfCancellation) {
        this.appointmentId = appointmentId;
        this.reasonOfCancellation = reasonOfCancellation;
    }

    // Getters and Setters
    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getReasonOfCancellation() {
        return reasonOfCancellation;
    }

    public void setReasonOfCancellation(String reasonOfCancellation) {
        this.reasonOfCancellation = reasonOfCancellation;
    }
}

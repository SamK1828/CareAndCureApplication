package com.cac.client.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.cac.client.model.AppointmentDTO;
import com.cac.client.model.CancelAppointmentDTO;
import com.cac.client.model.DoctorDTO;
import com.cac.client.model.Patient;
import com.cac.client.model.ScheduleAppointmentDTO;

import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/patient")
public class AppointmentController {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${base.url}")
    private String baseUrl;

    DoctorDTO doctorSession = null;

    Patient patientSession = null;

    String role = null;

    @ModelAttribute
    public void getPatient(@SessionAttribute(name = "patientObj", required = false) Patient patObj) {
        patientSession = patObj;

    }

    @ModelAttribute
    public void getRole(@SessionAttribute(name = "userRole", required = false) String userRole, Model model) {
        role = userRole;
        model.addAttribute("userRole", userRole);
    }

    @ModelAttribute
    public String checkLogin() {
        if (role == null)
            return "redirect:/";
        return null;
    }

    private boolean isAuthPatient(int patientId) {
        if (role == null)
            return false;
        if (role.equalsIgnoreCase("patient") && patientSession != null && patientId != patientSession.getPatientId())
            return false;
        return true;
    }

    @GetMapping("/{patientId}/appointments")
    public String showAppointmentsForPatient(@PathVariable int patientId, Model model) {

        if (!isAuthPatient(patientId)) {
            model.addAttribute("errorMessage", "Permission Denied. You are not authorized person");
            return "unauthorized";
        }

        String url = baseUrl +"/patient/" + patientId + "/appointments";
        try {
            ResponseEntity<List<AppointmentDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<AppointmentDTO>>() {
                    });
            List<AppointmentDTO> appointments = response.getBody();

            if (appointments == null || appointments.isEmpty()) {
                model.addAttribute("message", "No appointments found.");
            } else {
                model.addAttribute("patientId", patientId);
                model.addAttribute("appointments", appointments);
            }
            return "appointments";
        } catch (ResourceAccessException e) {
            model.addAttribute("errorMessage", "Unable to fetch appointments. Please check the backend service.");
            return "unauthorized";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "An unexpected error occurred while fetching appointments.");
            return "unauthorized";
        }
    }

    @GetMapping("/{patientId}/appointments/schedule")
    public String showScheduleAppointment(@PathVariable int patientId, Model model) {

        if (!isAuthPatient(patientId)) {
            model.addAttribute("errorMessage", "Permission Denied. You are not authorized person");
            return "unauthorized";
        }

        String url = baseUrl +"/api/doctors";
        try {
            // Make GET request to fetch doctors
            ResponseEntity<List<DoctorDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<DoctorDTO>>() {
                    });

            // Get the list of doctors from the response
            List<DoctorDTO> doctors = response.getBody();

            // Add attributes to the model
            model.addAttribute("scheduleAppointmentDTO", new ScheduleAppointmentDTO());
            model.addAttribute("patientId", patientId);
            model.addAttribute("doctors", doctors); // Add the list of doctors to the model

        } catch (Exception e) {
            model.addAttribute("errorMessage", "Failed to fetch doctors. Please try again.");
            return "unauthorized";
        }

        // Return the view
        return "appointment-schedule";
    }

    @PostMapping("/{patientId}/appointments/schedule")
    public String scheduleAppointment(@PathVariable int patientId,
            @ModelAttribute ScheduleAppointmentDTO scheduleAppointmentDTO,
            Model model) {

        if (!isAuthPatient(patientId)) {
            model.addAttribute("errorMessage", "Permission Denied. You are not authorized person");
            return "unauthorized";
        }

        if(patientId!= scheduleAppointmentDTO.getPatientId()){
            model.addAttribute("errorMessage", "Permission Denied. You are not authorized person");
            return "unauthorized";
        }
        

        String url = baseUrl +"/patient/" + patientId + "/appointments/schedule";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            HttpEntity<ScheduleAppointmentDTO> request = new HttpEntity<>(scheduleAppointmentDTO, headers);

            ResponseEntity<AppointmentDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    AppointmentDTO.class);

            AppointmentDTO createdAppointment = response.getBody();

            if (createdAppointment != null) {
                model.addAttribute("patientId", patientId);
                model.addAttribute("appointment", createdAppointment);
                return "appointment-confirmation";
            } else {
                model.addAttribute("errorMessage", "Some Internal Error Occur. \n Appointment could not be scheduled. Please try again.");
                return "unauthorized";
            }
        } catch (HttpStatusCodeException e) {
            // model.addAttribute("errorMessage", "Invalid input or scheduling conflict.
            // Please check your details.");
            String errorMessage = e.getResponseBodyAsString();
            model.addAttribute("errorMessage", errorMessage);
            return "unauthorized";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Failed to schedule the appointment. Please try again.");
            return "unauthorized";
        }
    }

    @GetMapping("/{patientId}/appointments/cancel/{appointmentId}")
    public String showCancelAppointment(@PathVariable int patientId,
            @PathVariable int appointmentId,
            Model model) {
                if (!isAuthPatient(patientId)) {
                    model.addAttribute("errorMessage", "Permission Denied. You are not authorized person");
                    return "unauthorized";
                }
        CancelAppointmentDTO cancelAppointmentDTO = new CancelAppointmentDTO();
        cancelAppointmentDTO.setAppointmentId(appointmentId); // Pre-fill the appointment ID
        model.addAttribute("cancelAppointmentDTO", cancelAppointmentDTO);
        model.addAttribute("patientId", patientId);
        return "appointment-cancel";
    }

    @PostMapping("/{patientId}/appointments/cancel/{appointmentId}")
    public String cancelAppointment(@PathVariable int patientId,
            @PathVariable Long appointmentId,
            @ModelAttribute CancelAppointmentDTO cancelAppointmentDTO,
            Model model) {
                 if (!isAuthPatient(patientId)) {
            model.addAttribute("errorMessage", "Permission Denied. You are not authorized person");
            return "unauthorized";
        }

        String url = baseUrl +"/patient/" + patientId + "/appointments/cancel";
        // String url = "http://localhost:8081/patient/" + patientId +
        // "/appointments/cancel/" + appointmentId;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            HttpEntity<CancelAppointmentDTO> request = new HttpEntity<>(cancelAppointmentDTO, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    request,
                    String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("Cancellation Successful");
                return "appointment-cancel-confirmation";
            } else {
                System.out.println("Cancellation Failed: " + response.getStatusCode());
                model.addAttribute("errorMessage", "Cancellation failed. Please try again.");
                return "unauthorized";
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "An unexpected error occurred during cancellation.");
            return "unauthorized"; // Return error page
        }
    }

    @GetMapping("/{patientId}/appointments/reschedule/{appointmentId}")
    public String showRescheduleAppointment(@PathVariable Long patientId,
            @PathVariable Long appointmentId,
            Model model) {
        model.addAttribute("patientId", patientId);
        model.addAttribute("appointmentId", appointmentId);
        return "appointment-reschedule";
    }

    @PostMapping("/{patientId}/appointments/reschedule/{appointmentId}")
    public String rescheduleAppointment(@PathVariable Long patientId,
            @PathVariable Long appointmentId,
            @RequestParam("newDate") String newDate,
            @RequestParam("newTime") String newTime,
            Model model) {
        String url = baseUrl +"/patient/" + patientId + "/appointments/reschedule/" + appointmentId;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            Map<String, String> requestPayload = new HashMap<>();
            requestPayload.put("newDate", newDate);
            requestPayload.put("newTime", newTime);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestPayload, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.PUT,
                    request,
                    String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                model.addAttribute("successMessage", "Appointment rescheduled successfully!");
                return "redirect:/patient/" + patientId + "/appointments"; // Redirect to appointment management page
            } else {
                model.addAttribute("errorMessage", "Failed to reschedule the appointment. Please try again.");
                return "unauthorized";
            }
        } catch (HttpClientErrorException e) {
            // model.addAttribute("errorMessage", "Invalid input or scheduling conflict.
            // Please check your details.");
            String errorMessage = e.getResponseBodyAsString();
            model.addAttribute("errorMessage", errorMessage);
            return "unauthorized";
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Failed to schedule the appointment. Please try again.");
            return "unauthorized";
        }
    }

    @GetMapping("/dailyAppointments")
    public String viewAppointmentsByDate(@RequestParam(value = "date", required = false) String date,
            HttpSession session, Model model) {
        if (role == null || !role.equalsIgnoreCase("ADMIN"))
            return "unauthorized";
        LocalDate selectedDate = date != null ? LocalDate.parse(date) : LocalDate.now();
        String url = baseUrl+"/patient/0/appointments/" + selectedDate;
        try {
            ResponseEntity<List<AppointmentDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<AppointmentDTO>>() {
                    });
            List<AppointmentDTO> appointments = response.getBody();
            model.addAttribute("appointments", appointments);
            model.addAttribute("selectedDate", selectedDate);
        } catch (HttpStatusCodeException e) {
            model.addAttribute("errorMessage", "Unable to fetch daily appointments: " + e.getResponseBodyAsString());
            return "statusPage";
        }

        return "dailyAppointments";
    }

	@GetMapping("/no-show-patients")
    public String viewNoShowAppointments(HttpSession session, Model model) {
        if (role == null || !role.equalsIgnoreCase("admin"))
            return "unauthorized";

        String url = baseUrl+ "/patient/0/appointments/no-show";
System.out.println("hello");
        try {
            ResponseEntity<List<AppointmentDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<AppointmentDTO>>() {
                    });
                    System.out.println("hello");
            List<AppointmentDTO> noShowAppointments = response.getBody();
			System.out.println(noShowAppointments);
            model.addAttribute("noShowAppointments", noShowAppointments);
        } catch (HttpStatusCodeException e) {
            model.addAttribute("errorMessage", "Unable to fetch no-show appointments: " + e.getResponseBodyAsString());
            return "statusPage";
        }

        return "noShowAppointments";
    }
}